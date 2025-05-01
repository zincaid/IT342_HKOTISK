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
        val decrementButton: Button = view.findViewById(R.id.decrementButton)
        val quantityDisplay: TextView = view.findViewById(R.id.quantityDisplay)
        val incrementButton: Button = view.findViewById(R.id.incrementButton)
        val addToCartButton: Button = view.findViewById(R.id.addToCartButton)
        val loadingSpinner: ProgressBar = view.findViewById(R.id.loadingSpinner)
        val availabilityStatus: TextView = view.findViewById(R.id.availabilityStatus)
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
        holder.productPrice.text = "₱${product.price}"
        holder.productDescription.text = product.description

        // Handle availability status
        holder.availabilityStatus.visibility = if (!product.available) View.VISIBLE else View.GONE
        holder.addToCartButton.isEnabled = product.available
        
        // Load image using Glide
        Glide.with(context)
            .load(product.productImage)
            .centerCrop()
            .into(holder.productImage)

        // Set up quantity controls
        var quantity = 1
        holder.quantityDisplay.text = quantity.toString()

        holder.decrementButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                holder.quantityDisplay.text = quantity.toString()
            }
        }

        holder.incrementButton.setOnClickListener {
            if (quantity < product.quantity) {
                quantity++
                holder.quantityDisplay.text = quantity.toString()
            } else {
                Toast.makeText(context, "Maximum available quantity reached", Toast.LENGTH_SHORT).show()
            }
        }

        // Add to cart button click handler with loading state
        holder.addToCartButton.setOnClickListener { button ->
            button.visibility = View.INVISIBLE
            holder.loadingSpinner.visibility = View.VISIBLE

            if (!product.available) {
                button.visibility = View.VISIBLE
                holder.loadingSpinner.visibility = View.GONE
                Toast.makeText(context, "Product is not available", Toast.LENGTH_SHORT).show()
                button.visibility = View.VISIBLE
                holder.loadingSpinner.visibility = View.GONE
                return@setOnClickListener
            }
            
            val quantity = holder.quantityDisplay.text.toString().toIntOrNull() ?: 1
            
            if (quantity > product.quantity) {
                Toast.makeText(context, "Not enough stock available", Toast.LENGTH_SHORT).show()
                button.visibility = View.VISIBLE
                holder.loadingSpinner.visibility = View.GONE
                return@setOnClickListener
            }
            
            val price = product.price

            val cartRequest = CartRequest(
                productId = product.productId,
                quantity = quantity,
                price = price
            )

            RetrofitClient.createProductService(context)
                .addToCart(cartRequest)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    )

                    {
                        button.visibility = View.VISIBLE
                        holder.loadingSpinner.visibility = View.GONE
                        Log.d("ProductAdapter", "Response code: ${response.code()}")
                        Log.d("ProductAdapter", "Raw response: ${response.raw()}")
                        
                        val code = response.code()
                        Log.d("ProductAdapter", "Headers: ${response.headers()}")
                        
                        when (code) {
                            200, 201 -> {

                                Toast.makeText(
                                    context, "Added to cart successfully",
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
                        button.visibility = View.VISIBLE
                        holder.loadingSpinner.visibility = View.GONE
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
