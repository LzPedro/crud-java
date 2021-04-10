/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lzpedro.stocksapi.controller;

import com.github.lzpedro.stocksapi.model.Stock;
import com.github.lzpedro.stocksapi.service.StockService;
import java.util.List;
import lombok.var;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
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
	
	private static final Logger logger = Logger.getLogger(StockController.class);
	
	@Autowired
	private StockService stockService;
	
	@GetMapping
	public ResponseEntity<List<Stock>> find() {
		if(stockService.find().isEmpty()) {
			return ResponseEntity.notFound().build(); 
		}
		logger.info(stockService.find());
		return ResponseEntity.ok(stockService.find());
	}
	
	@DeleteMapping
	public ResponseEntity<Boolean> delete() {
		try {
			stockService.delete();
			return ResponseEntity.noContent().build();
		}catch(Exception e) {
			logger.error(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@PostMapping
	@ResponseBody
	public ResponseEntity<Stock> create(@RequestBody JSONObject travel) {
		try {
			if(stockService.isJSONValid(travel.toString())) {
				Stock travelCreated = stockService.create(travel);
				var uri = ServletUriComponentsBuilder.fromCurrentRequest().path(travelCreated.getOrderNumber()).build().toUri();
				
				if(stockService.isStartDateGreaterThanEndDate(travelCreated)){
					logger.error("The start date is greater than end date.");
					return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
				}else {
					stockService.add(travelCreated);
					return ResponseEntity.created(uri).body(null);
				}
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			logger.error("JSON fields are not parsable. " + e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
		}
	}
	
	@PutMapping(path = "/{id}", produces = { "application/json" })
	public ResponseEntity<Stock> update(@PathVariable("id") long id, @RequestBody JSONObject travel) {
		try {
			if(stockService.isJSONValid(travel.toString())) {
				Stock travelToUpdate = stockService.findById(id);
				if(travelToUpdate == null){
					logger.error("Stock not found.");
					return ResponseEntity.notFound().build(); 
				}else {
					Stock travelToUpdate = stockService.update(travelToUpdate, travel);
					return ResponseEntity.ok(travelToUpdate);
				}
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			logger.error("JSON fields are not parsable." + e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
		}
	}
}