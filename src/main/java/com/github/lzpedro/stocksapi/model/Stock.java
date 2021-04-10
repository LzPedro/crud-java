/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lzpedro.stocksapi.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author PedroM
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private String stockName;
    private List<Double> quotes =  new ArrayList<>();

    public void add(Double quote) {
        // Add a String to my list of String
        quotes.add(quote);
    }
}
