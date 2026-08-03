package com.tpe.controller;

import com.tpe.dto.DataflowDTO;
import com.tpe.eception.ResourceNotFoundException;
import com.tpe.model.*;

import com.tpe.service.DataflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dataflows")
public class DataflowController {
    @Autowired
    private  DataflowService dataflowService;

    @GetMapping
    public List<DataflowDTO>  getAll() {
        List<Dataflow> dataflows =dataflowService.findAll();
        List<DataflowDTO> dataflowList =
        dataflows.stream().map(x-> new DataflowDTO(x.getRuleId()))
                .collect(Collectors.toList());
        return dataflowList;
    }
    @GetMapping("/{id}")
    public DataflowDTO getById(@PathVariable ("id") Long id) {
        Dataflow dataflow =dataflowService.findById(id);
        if (dataflow == null) {
            throw new ResourceNotFoundException("Dataflow not found with id: " + id);
        }
        return new DataflowDTO (dataflow.getRuleId());
    }

    @PostMapping
    public ResponseEntity<DataflowDTO> createDataflow(@Validated @RequestBody DataflowDTO dataflowDTO) {
        Dataflow dataflow = new Dataflow();
        dataflow.setRuleId(dataflowDTO.ruleId());
        // Save to database
        Dataflow savedDataflow = dataflowService.saveAndFlush(dataflow);
        // Build the DTO response
        DataflowDTO responseBody = new DataflowDTO(savedDataflow.getRuleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

}
