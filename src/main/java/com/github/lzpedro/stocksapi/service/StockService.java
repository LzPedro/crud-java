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

    private Double parseQuote(JSONObject stock) {
        try {
            return new Double((String) stock.get("quote"));
        } catch (JSONException ex) {
            Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void setStockValues(JSONObject jsonStock, Stock stock) {
        if (parseQuote(jsonStock) != null) {
            stock.add(parseQuote(jsonStock));
        }
    }

    public Stock create(JSONObject jsonStock) {
        try {
            Stock stock = new Stock();
            stock.setStockName((String) jsonStock.get("name"));
            setStockValues(jsonStock, stock);

            return stock;
        } catch (JSONException ex) {
            Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Stock update(Stock stock, JSONObject jsonStock) {
        setStockValues(jsonStock, stock);
        return stock;
    }

    public void add(Stock stock) {
        createStockList();
        stocks.add(stock);
    }

    public List<Stock> find() {
        createStockList();
        return stocks;
    }

    public Stock findByName(String stockName) {
        return stocks.stream().filter(t -> stockName.equals(t.getStockName())).collect(Collectors.toList()).get(0);
    }

    public void delete() {
        stocks.clear();
    }

    public void clearObjects() {
        stocks = null;
    }

    public boolean isJSONValid(String jsonInString) {
        try {
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }
}
