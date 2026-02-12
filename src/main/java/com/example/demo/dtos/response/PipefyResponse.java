package com.example.demo.dtos.response;



import lombok.Data;
import java.util.List;

@Data
public class PipefyResponse {
    private DataResponse data;
    private List<Object> errors;
}