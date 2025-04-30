package com.CSIT321.Hkotisk.Controller;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import com.CSIT321.Hkotisk.Constant.ResponseCode;
import com.CSIT321.Hkotisk.Constant.WebConstants;
import com.CSIT321.Hkotisk.DTO.cartDTO;
import com.CSIT321.Hkotisk.DTO.OrderDetailsDTO;
import com.CSIT321.Hkotisk.Entity.CartEntity;
import com.CSIT321.Hkotisk.Entity.OrderEntity;
import com.CSIT321.Hkotisk.Entity.ProductEntity;
import com.CSIT321.Hkotisk.Entity.User;
import com.CSIT321.Hkotisk.Exception.CartCustomException;
import com.CSIT321.Hkotisk.Exception.OrderCustomException;
import com.CSIT321.Hkotisk.Exception.ProductCustomException;
import com.CSIT321.Hkotisk.Exception.UserCustomException;
import com.CSIT321.Hkotisk.Handler.OrderWebSocketHandler;
import com.CSIT321.Hkotisk.Repository.CartRepository;
import com.CSIT321.Hkotisk.Repository.OrderRepository;
import com.CSIT321.Hkotisk.Repository.ProductRepository;
import com.CSIT321.Hkotisk.Repository.UserRepository;
import com.CSIT321.Hkotisk.Response.CartResponse;
import com.CSIT321.Hkotisk.Response.ProductResponse;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RestController
@RequestMapping("/user")
public class UserController extends TextWebSocketHandler {

    private static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository prodRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private OrderRepository ordRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private WebSocketSession webSocketSession;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.webSocketSession = session;
    }


    // Lists All Products
    @GetMapping("/product")
    public ResponseEntity<ProductResponse> getProducts(Authentication auth) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.LIST_SUCCESS_MESSAGE);
            resp.setOblist(prodRepo.findAll());
        } catch (Exception e) {
            throw new ProductCustomException("Unable to retrieve products, please try again");
        }
        return new ResponseEntity<ProductResponse>(resp, HttpStatus.OK);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductEntity> getProductsById(@PathVariable int id) {
        ProductEntity products = prodRepo.findByProductId(id);
        return ResponseEntity.ok(products);
    }


    // Adds a product to the cart
    @PostMapping("/cart")
    public ResponseEntity<ServerResponse> addToCart(@Valid @RequestBody cartDTO cart, Authentication auth) throws IOException {
        ServerResponse resp = new ServerResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            ProductEntity cartItem = prodRepo.findByProductId(cart.getProductId());
            if (cartItem == null) {
                throw new ProductCustomException("Product not found");
            }

            // Check if product is available and has enough quantity
            if (!cartItem.isAvailable()) {
                throw new CartCustomException("Product is not available");
            }
            if (cartItem.getQuantity() < cart.getQuantity()) {
                throw new CartCustomException("Not enough stock available");
            }

            // Check if the item already exists in the cart
            Optional<CartEntity> existingCartItem = cartRepo.findByEmailAndProductIdAndIsOrderedFalse(
                    loggedUser.getEmail(), cart.getProductId());

            if (existingCartItem.isPresent()) {
                // Update the quantity of the existing item
                CartEntity confirmedExistingCartItem = existingCartItem.get();
                confirmedExistingCartItem.setQuantity(confirmedExistingCartItem.getQuantity() + cart.getQuantity());
                cartRepo.save(confirmedExistingCartItem);
            } else {
                // Create a new cart item
                CartEntity buf = new CartEntity();
                buf.setEmail(loggedUser.getEmail());
                buf.setQuantity(cart.getQuantity());
                buf.setPrice(cart.getPrice() != 0.0 ? cart.getPrice() : cartItem.getPrice());
                buf.setProductId(cart.getProductId());
                buf.setProductCategory(cartItem.getCategory());
                buf.setProductName(cartItem.getProductName());
                buf.setProductImage(cartItem.getProductImage());
                buf.setDateAdded(new Date());

                cartRepo.save(buf);
            }

            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.CART_UPD_MESSAGE_CODE);
        } catch (UserCustomException e) {
            logger.severe("User not found: " + e.getMessage());
            throw new CartCustomException("Unable to add product to cart, user not found");
        } catch (ProductCustomException e) {
            logger.severe("Product not found: " + e.getMessage());
            throw new CartCustomException("Unable to add product to cart, product not found");
        } catch (CartCustomException e) {
            logger.severe("Cart error: " + e.getMessage());
            throw new CartCustomException(e.getMessage());
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
            throw new CartCustomException("Unable to add product to cart, please try again");
        }
        return new ResponseEntity<ServerResponse>(resp, HttpStatus.OK);
    }

    // Query to get all carts belonging to an email
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> viewCart(Authentication auth) throws IOException {
        logger.info("Inside View cart request method");
        System.out.println(auth.getName());
        CartResponse resp = new CartResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException("User not found: " + auth.getName()));
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.VW_CART_MESSAGE);
            resp.setOblist(cartRepo.findAllByEmailAndIsOrderedFalse(loggedUser.getEmail()));
        } catch (Exception e) {
            logger.severe("Error retrieving cart items: " + e.getMessage());
            throw new CartCustomException("Unable to retrieve cart items, please try again");
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PutMapping("/cart")
    public ResponseEntity<CartResponse> updateCart(@RequestBody HashMap<String, String> cart, Authentication auth)
            throws IOException {

        CartResponse resp = new CartResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            CartEntity studentCart = cartRepo.findByCartIdAndEmail(Integer.parseInt(cart.get("id")), loggedUser.getEmail());
            studentCart.setQuantity(Integer.parseInt(cart.get("quantity")));
            cartRepo.save(studentCart);
            List<CartEntity> studentCarts = cartRepo.findByEmail(loggedUser.getEmail());
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.UPD_CART_MESSAGE);
            resp.setOblist(studentCarts);
        } catch (Exception e) {
            throw new CartCustomException("Unable to update cart items, please try again");
        }

        return new ResponseEntity<CartResponse>(resp, HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<CartResponse> delCart(@RequestParam(name = WebConstants.BUF_ID) String cartId,
                                                Authentication auth) throws IOException {

        CartResponse resp = new CartResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            cartRepo.deleteByCartIdAndEmail(Integer.parseInt(cartId), loggedUser.getEmail());
            List<CartEntity> studentCarts = cartRepo.findByEmail(loggedUser.getEmail());
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.DEL_CART_SUCCESS_MESSAGE);
            resp.setOblist(studentCarts);
        } catch (Exception e) {
            throw new CartCustomException("Unable to delete cart items, please try again");
        }
        return new ResponseEntity<CartResponse>(resp, HttpStatus.OK);
    }

    // Get user's orders with details
    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders(Authentication auth) throws IOException {
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            
            List<OrderEntity> userOrders = ordRepo.findByEmail(loggedUser.getEmail());
            List<OrderDetailsDTO> orderDetails = new ArrayList<>();
            
            for (OrderEntity order : userOrders) {
                OrderDetailsDTO details = new OrderDetailsDTO();
                details.setOrderId(order.getOrderId());
                details.setEmail(order.getEmail());
                details.setOrderStatus(order.getOrderStatus());
                details.setOrderDate(order.getOrderDate());
                details.setTotalCost(order.getTotalCost());
                
                // Get cart items for this order
                List<CartEntity> items = cartRepo.findAllByOrderId(order.getOrderId());
                details.setItems(items);
                
                orderDetails.add(details);
            }
            
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            throw new OrderCustomException("Unable to retrieve orders, please try again");
        }
    }

    // Get user profile
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication auth) {
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            
            Map<String, String> profile = new HashMap<>();
            profile.put("username", loggedUser.getUsername());
            profile.put("email", loggedUser.getEmail());
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            throw new UserCustomException("Unable to retrieve user profile, please try again");
        }
    }

    // Update user information
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, String> updates, Authentication auth) {
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            
            // Update allowed fields if provided
            if (updates.containsKey("username")) {
                loggedUser.setUsername(updates.get("username"));
            }
            if (updates.containsKey("email")) {
                String newEmail = updates.get("email");
                if (!newEmail.equals(loggedUser.getEmail())) {
                    // Check if new email is already used by another user
                    Optional<User> existingUser = userRepo.findByEmail(newEmail);
                    if (existingUser.isPresent()) {
                        throw new UserCustomException("Email already in use");
                    }
                    loggedUser.setEmail(newEmail);
                }
            }
            if (updates.containsKey("password")) {
                // Encrypt password before saving
                String encodedPassword = passwordEncoder.encode(updates.get("password"));
                loggedUser.setPassword(encodedPassword);
            }
            
            // Preserve existing role and enabled status
            User savedUser = userRepo.save(loggedUser);
            
            // Don't send password in response
            savedUser.setPassword(null);
            return ResponseEntity.ok(savedUser);
        } catch (UserCustomException e) {
            throw e; // Re-throw custom exceptions
        } catch (Exception e) {
            throw new UserCustomException("Unable to update user profile, please try again");
        }
    }

    @PostMapping("/order")
    public ResponseEntity<ServerResponse> placeOrder(Authentication auth) throws IOException {
        ServerResponse resp = new ServerResponse();
        try {
            User loggedUser = userRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new UserCustomException(auth.getName()));
            OrderEntity po = new OrderEntity();
            po.setEmail(loggedUser.getEmail());
            Date date = new Date();
            po.setOrderDate(date);
            po.setOrderStatus(ResponseCode.ORD_STATUS_CODE);
            double total = 0;
            List<CartEntity> studentCarts = cartRepo.findAllByEmailAndIsOrderedFalse(loggedUser.getEmail());
            if (studentCarts.isEmpty()) {
                throw new OrderCustomException("No items in cart to place order");
            }
            for (CartEntity studentCart : studentCarts) {
                total += studentCart.getQuantity() * studentCart.getPrice();
            }
            po.setTotalCost(total);
            OrderEntity res = ordRepo.save(po);
            studentCarts.forEach(studentCart -> {
                studentCart.setOrderId(res.getOrderId());
                studentCart.setOrdered(true);
                cartRepo.save(studentCart);

                // Update product quantity and availability
                ProductEntity product = prodRepo.findByProductId(studentCart.getProductId());
                product.setQuantity(Math.max(0, product.getQuantity() - studentCart.getQuantity()));
                // updateAvailability() will be called by @PreUpdate
                prodRepo.save(product);
            });
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.ORD_SUCCESS_MESSAGE);

            // Send WebSocket message
            OrderWebSocketHandler.sendMessageToAll("New order placed: " + res.getOrderId());


        } catch (Exception e) {
            throw new OrderCustomException("Unable to place order, please try again later");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
