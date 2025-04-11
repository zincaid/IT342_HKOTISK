package com.CSIT321.Hkotisk.Config;

import com.CSIT321.Hkotisk.Handler.OrderWebSocketHandler;
import com.CSIT321.Hkotisk.Handler.ProductWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new OrderWebSocketHandler(), "/ws/orders").setAllowedOrigins("*");
        registry.addHandler(new ProductWebSocketHandler(), "/ws/products").setAllowedOrigins("*");
    }
}