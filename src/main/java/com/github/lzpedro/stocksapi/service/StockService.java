/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lzpedro.stocksapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lzpedro.stocksapi.model.Stock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author PedroM
 */
@Service
public class StockService {

    private List<Stock> stocks;

    public void createStockList() {
        if (stocks == null) {
            stocks = new ArrayList<>();
        }
    }

    private void setStockValues(JSONObject jsonStock, Stock stock) {
        try {
            //parse String
            String quotes = (String) jsonStock.getString("quotes");
            quotes = quotes.replace("[", "");
            quotes = quotes.replace("]", "");
            List<String> StringList = new ArrayList<>(Arrays.asList(quotes.split(",")));
            for (int i = 0; i < StringList.size(); ++i) {
                //transform from String to double and add to quotes of stock
                 stock.add(Double.parseDouble(StringList.get(i)));
                 Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, StringList.get(i));
            }
        } catch (JSONException ex) {
            Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Stock create(JSONObject jsonStock) {
        try {
            Stock stock = new Stock();
            //set stockName given Json name value
            stock.setStockName((String) jsonStock.getString("name"));
            //add quote to quotes
            setStockValues(jsonStock, stock);

            return stock;
        } catch (JSONException ex) {
            Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Stock update(Stock stock, JSONObject jsonStock) {
        //add quote to quotes
        setStockValues(jsonStock, stock);
        return stock;
    }

    public void add(Stock stock) {
        //create Stock list
        createStockList();
        stocks.add(stock);
    }

    public List<Stock> find() {
        //return all stocks
        createStockList();
        return stocks;
    }

    public Stock findByName(String stockName) {
        //return stock by stockName
        return stocks.stream().filter(t -> stockName.equals(t.getStockName())).collect(Collectors.toList()).get(0);
    }
    
     public void deleteByName(String stockName) {
        //remove stock by stockName
        stocks.removeIf( name -> name.getStockName().equals(stockName));
    }

    public void deleteAll() {
        //delete all stocks
        stocks.clear();
    }

    public boolean isJSONValid(String jsonInString) {
        try {
            //verify if the String is in a valid JSON format
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }
}
