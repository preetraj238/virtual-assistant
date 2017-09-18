package com.dienen.virtualassistant.service;

import com.dienen.virtualassistant.helper.SpeechToTextHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by preetraj on 9/15/17.
 */
@CrossOrigin(origins = "${cors.origins}")
@RestController
@RequestMapping("/speech")
public class SpeechToTextService {

    @Autowired
    private SpeechToTextHelper speechToTextHelper;

    @ApiOperation(value = "text", nickname = "text")
    @RequestMapping(method = RequestMethod.GET, path = "/text")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = List.class)})
    public ResponseEntity<?> getAllBidEntities() throws Exception {

        return ResponseEntity.ok(speechToTextHelper.getSpeechResponse());
    }

    @ApiOperation(value = "text", nickname = "text")
    @RequestMapping(method = RequestMethod.GET, path = "/analyze")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = List.class)})
    public ResponseEntity<?> runAnalyzer(@RequestParam("text") String text) throws Exception {

        return ResponseEntity.ok(speechToTextHelper.runAnalyzer(text));
    }

    @ApiOperation(value = "call", nickname = "call")
    @RequestMapping(method = RequestMethod.GET, path = "/call")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = List.class)})
    public ResponseEntity<?> call(@RequestParam("text") String text) throws Exception {

        return ResponseEntity.ok(speechToTextHelper.call(text));
    }
}
