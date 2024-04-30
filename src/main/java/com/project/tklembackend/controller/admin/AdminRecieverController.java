package com.project.tklembackend.controller.admin;

import com.project.tklembackend.dto.MessageDTO;
import com.project.tklembackend.dto.ReceptorDTO;
import com.project.tklembackend.dto.RecieverDTO;
import com.project.tklembackend.service.AuthService;
import com.project.tklembackend.service.GlobalService;
import com.project.tklembackend.service.admin.AdminRecieverService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AdminRecieverController {
    private final AdminRecieverService adminRecieverService;
    private final GlobalService globalService;



    @GetMapping("admin/reciever/all")
    public ResponseEntity<List<RecieverDTO>> getAllRecievers(){
        return new ResponseEntity<>(adminRecieverService.getAllRecievers(), HttpStatus.OK);
    }

    @PostMapping("admin/reciever/add")
    public ResponseEntity<RecieverDTO> addReciever(@RequestBody RecieverDTO RecieverDTO){
        return new ResponseEntity<>(adminRecieverService.addReciever(RecieverDTO), HttpStatus.OK);
    }

    @PostMapping("admin/reciever/edit")
    public ResponseEntity<RecieverDTO> editReciever(@RequestBody RecieverDTO RecieverDTO){
        return new ResponseEntity<>(adminRecieverService.editReciever(RecieverDTO), HttpStatus.OK);
    }
    @PostMapping("admin/reciever/delete")
    public ResponseEntity<Map<String,String>> deleteReciever(@RequestBody Map<String,Long> request){
        adminRecieverService.deleteRecievers(request.get("id"));
        return new ResponseEntity<>(globalService.responseBuilder("entity deleted Successfully"), HttpStatus.OK);
    }

    @PostMapping("admin/reciever/deleteall")
    public ResponseEntity<Map<String,String>> deleteReciever(@RequestBody List<Map<String,Long>> request){
        request.forEach(id->{adminRecieverService.deleteRecievers(id.get("id"));});
        return new ResponseEntity<>(globalService.responseBuilder("entity deleted Successfully"), HttpStatus.OK);
    }

    @PostMapping("reciever/call")
    public ResponseEntity<Map<String,String>> callReciever(@RequestBody Map<String,String> request) throws IOException {
        adminRecieverService.callReceiver(request.get("massarCode"));
        return new ResponseEntity<>(globalService.responseBuilder("Reciever has been notified successfully"), HttpStatus.OK);
    }
    @PostMapping("reciever/accept")
    public ResponseEntity<Map<String,String>> acceptReciever(@RequestBody MessageDTO messageDTO) throws IOException {
        adminRecieverService.acceptReciever(messageDTO);
        return new ResponseEntity<>(globalService.responseBuilder("Student has been logged"), HttpStatus.OK);
    }
    @PostMapping("admin/reciever/edit-password")
    public ResponseEntity<Map<String,String>> editReceiverPassword(@RequestBody RecieverDTO recieverDTO){
        adminRecieverService.editReceiverPassword(recieverDTO);
        Map<String,String> response = new HashMap<>();
        response.put("message","Receiver Password has successfully modified");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

