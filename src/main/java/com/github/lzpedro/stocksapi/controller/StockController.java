/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lzpedro.stocksapi.controller;

import com.github.lzpedro.stocksapi.model.Stock;
import com.github.lzpedro.stocksapi.service.StockService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.var;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author PedroM
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    //private static final Logger logger = Logger.getLogger(StockController.class);
    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<List<Stock>> find() {
        if (stockService.find().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        //logger.info(stockService.find());
        return ResponseEntity.ok(stockService.find());
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete() {
        try {
            stockService.delete();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            //logger.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Stock> create(@RequestBody String stockString) {
        try {
            JSONObject stock = new JSONObject(stockString);
            try {
                logger.error("quotes");
                logger.error((String) stock.getString("quotes"));
                if (stockService.isJSONValid(stock.toString())) {
                    Stock stockCreated = stockService.create(stock);
                    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path(stockCreated.getStockName()).build().toUri();

                    stockService.add(stockCreated);
                    return ResponseEntity.created(uri).body(null);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } catch (Exception e) {
                //logger.error("JSON fields are not parsable. " + e);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
            }
        } catch (JSONException err) {
            logger.error("Error", err.toString());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }

    @PutMapping(path = "/{name}", produces = {"application/json"})
    public ResponseEntity<Stock> update(@PathVariable("name") String name, @RequestBody String stockString) {
        try {
            JSONObject stock = new JSONObject(stockString);
            try {
                if (stockService.isJSONValid(stock.toString())) {
                    Stock stockToUpdate = stockService.findByName(name);
                    if (stockToUpdate == null) {
                        //logger.error("Stock not found.");
                        return ResponseEntity.notFound().build();
                    } else {
                        stockToUpdate = stockService.update(stockToUpdate, stock);
                        return ResponseEntity.ok(stockToUpdate);
                    }
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } catch (Exception e) {
                //logger.error("JSON fields are not parsable." + e);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
            }
        } catch (JSONException ex) {
            java.util.logging.Logger.getLogger(StockController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }
}
