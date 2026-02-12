package com.example.demo.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GraphQLRequest {
    private String query;
}