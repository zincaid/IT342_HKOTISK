package com.edu.cit.hkotisk

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.CartRequest
import com.edu.cit.hkotisk.data.model.CartResponse
import com.edu.cit.hkotisk.data.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductAdapter(
    private var products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productDescription: TextView = view.findViewById(R.id.productDescription)
        val sizeSpinner: Spinner = view.findViewById(R.id.sizeSpinner)
        val quantityInput: EditText = view.findViewById(R.id.quantityInput)
        val addToCartButton: Button = view.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context
        
        holder.productName.text = product.productName
        holder.productPrice.text = "₱${product.prices.firstOrNull() ?: 0.0}"
        holder.productDescription.text = product.description
        
        // Load image using Glide
        Glide.with(context)
            .load(product.productImage)
            .centerCrop()
            .into(holder.productImage)

        // Set up size spinner
        val sizeAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            product.sizes
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        holder.sizeSpinner.adapter = sizeAdapter

        // Set default quantity and add validation
        holder.quantityInput.setText("1")
        holder.quantityInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = holder.quantityInput.text.toString()
                val quantity = input.toIntOrNull() ?: 0
                if (quantity < 1) {
                    holder.quantityInput.setText("1")
                    Toast.makeText(context, "Quantity must be at least 1", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Add to cart button click handler with loading state
        holder.addToCartButton.setOnClickListener { button ->
            button.isEnabled = false
            val selectedSize = holder.sizeSpinner.selectedItem as String
            val quantity = holder.quantityInput.text.toString().toIntOrNull() ?: 1
            val price = product.prices.firstOrNull()?.toDouble()?.toInt() ?: 0

            val cartRequest = CartRequest(
                productId = product.productId,
                quantity = quantity,
                price = price,
                size = selectedSize
            )

            RetrofitClient.createProductService(context)
                .addToCart(cartRequest)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    )

                    {
                        button.isEnabled = true
                        Log.d("ProductAdapter", "Response code: ${response.code()}")
                        Log.d("ProductAdapter", "Raw response: ${response.raw()}")
                        
                        val code = response.code()
                        Log.d("ProductAdapter", "Headers: ${response.headers()}")
                        
                        when (code) {
                            200, 201 -> {
                                val cartResponse = response.body()
                                Toast.makeText(
                                    context,
                                    cartResponse?.message ?: "Added to cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            401 -> {
                                Toast.makeText(
                                    context,
                                    "Please login again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            302 -> {
                                val location = response.headers()["Location"]
                                Log.e("ProductAdapter", "Redirect location: $location")
                                Toast.makeText(
                                    context,
                                    "Server error: Unable to process request",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else -> {
                                try {
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("ProductAdapter", "Error body: $errorBody")
                                    Toast.makeText(
                                        context,
                                        "Failed to add to cart (${response.code()}): ${response.message()}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (e: Exception) {
                                    Log.e("ProductAdapter", "Error parsing error response", e)
                                    Toast.makeText(
                                        context,
                                        "Failed to add to cart: Unknown error",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        button.isEnabled = true
                        Log.e("ProductAdapter", "Network error", t)
                        val errorMessage = when {
                            t.message?.contains("ProtocolException") == true -> "Server error: Too many redirects"
                            t.message?.contains("ConnectException") == true -> "Connection failed: Server may be down"
                            t.message?.contains("SocketTimeoutException") == true -> "Request timed out"
                            else -> "Network error: ${t.message}"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
