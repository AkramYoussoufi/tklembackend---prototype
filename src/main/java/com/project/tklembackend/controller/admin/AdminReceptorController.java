package com.project.tklembackend.controller.admin;

import com.project.tklembackend.dto.ParentDTO;
import com.project.tklembackend.dto.ReceptorDTO;
import com.project.tklembackend.service.GlobalService;
import com.project.tklembackend.service.admin.AdminReceptorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/receptor")
@AllArgsConstructor
public class AdminReceptorController {
    private final AdminReceptorService adminReceptorService;
    private final GlobalService globalService;

    @GetMapping("/all")
    public ResponseEntity<List<ReceptorDTO>> getAllReceptors(){
        return new ResponseEntity<>(adminReceptorService.getAllReceptors(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ReceptorDTO> addReceptor(@RequestBody ReceptorDTO receptorDTO){
        return new ResponseEntity<>(adminReceptorService.addReceptor(receptorDTO), HttpStatus.OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<ReceptorDTO> editReceptor(@RequestBody ReceptorDTO receptorDTO){
        return new ResponseEntity<>(adminReceptorService.editReceptor(receptorDTO), HttpStatus.OK);
    }
    @PostMapping("/delete")
    public ResponseEntity<Map<String,String>> deleteReceptor(@RequestBody Map<String,Long> request){
        adminReceptorService.deleteReceptors(request.get("id"));
        return new ResponseEntity<>(globalService.responseBuilder("entity deleted Successfully"), HttpStatus.OK);
    }

    @PostMapping("/deleteall")
    public ResponseEntity<Map<String,String>> deleteReceptor(@RequestBody List<Map<String,Long>> request){
        request.forEach(id->{adminReceptorService.deleteReceptors(id.get("id"));});
        return new ResponseEntity<>(globalService.responseBuilder("entity deleted Successfully"), HttpStatus.OK);
    }

    @PostMapping("/edit-password")
    public ResponseEntity<Map<String,String>> editParentPassword(@RequestBody ReceptorDTO receptorDTO){
        adminReceptorService.editReceptorPassword(receptorDTO);
        Map<String,String> response = new HashMap<>();
        response.put("message","Receptor Password has successfully modified");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
