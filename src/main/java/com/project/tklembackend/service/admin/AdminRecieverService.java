package com.project.tklembackend.service.admin;

import com.project.tklembackend.config.TradeWebSocketHandler;
import com.project.tklembackend.dto.MessageDTO;
import com.project.tklembackend.dto.ParentDTO;
import com.project.tklembackend.dto.RecieverDTO;
import com.project.tklembackend.model.*;
import com.project.tklembackend.repository.*;
import com.project.tklembackend.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminRecieverService {
    private final RecieverRepository RecieverRepository;
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FormationRepository formationRepository;
    private final StudentRepository studentRepository;
    private final TradeWebSocketHandler tradeWebSocketHandler;
    private final AuthService authService;
    private final ParentRepository parentRepository;
    private final StudentLogRepository studentLogRepository;
    @Transactional
    public List<RecieverDTO> getAllRecievers(){
        return RecieverRepository.findAll().stream().map(this::convertToDTO).toList();
    }
    @Transactional
    public RecieverDTO addReciever(RecieverDTO RecieverDTO) {
        return buildReciever(RecieverDTO);
    }
    @Transactional
    public RecieverDTO editReciever(RecieverDTO recieverDTO) {
        try {
            if (RecieverRepository.existsById(recieverDTO.getId())) {
                Reciever existingReciever = RecieverRepository.findById(recieverDTO.getId()).get();
                updateRecieverFromDTO(existingReciever, recieverDTO);
                RecieverRepository.save(existingReciever);
                return convertToDTO(existingReciever);
            } else {
                throw new NoSuchElementException("Reciever you are trying to edit doesn't exist");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void updateRecieverFromDTO(Reciever reciever, RecieverDTO recieverDTO) {
        reciever.setName(recieverDTO.getName());
        // Update other fields as needed...
        reciever.setEmail(recieverDTO.getEmail());
        reciever.setFormation(formationRepository.findByName(recieverDTO.getFormationName())
                .orElseThrow(() -> new NoSuchElementException("Formation not found")));

        // Update user entity if needed
        UserEntity userEntity = reciever.getUserEntity();
        userEntity.setEmail(recieverDTO.getEmail());
        userEntity.setEnabled(recieverDTO.getStatus());
        //userEntity.setPassword(bCryptPasswordEncoder.encode(recieverDTO.getPassword()));
        userEntityRepository.save(userEntity);

    }
    @Transactional
    public void deleteRecievers(Long id){
        UserEntity userEntity = RecieverRepository.findById(id).get().getUserEntity();
        userEntityRepository.delete(userEntity);
        RecieverRepository.deleteById(id);
    }

    private RecieverDTO convertToDTO(Reciever Reciever) {
        RecieverDTO RecieverDTO = modelMapper.map(Reciever, RecieverDTO.class);
        RecieverDTO.setEmail(Reciever.getEmail());
        RecieverDTO.setPassword(Reciever.getUserEntity().getPassword());
        RecieverDTO.setStatus(Reciever.getUserEntity().getEnabled());
        RecieverDTO.setFormationName(Reciever.getFormation().getName());
        return RecieverDTO;
    }

    private RecieverDTO buildReciever(RecieverDTO RecieverDTO){
        Reciever Reciever = new Reciever();
        if(Reciever.getId() != null){
            Reciever.setId(Reciever.getId());
        }
        Reciever.setName(RecieverDTO.getName());
        Reciever.setEmail(RecieverDTO.getEmail());
        Reciever.setFormation(formationRepository.findByName(RecieverDTO.getFormationName()).orElseThrow(() -> new NoSuchElementException("Formation not found")));
        RecieverRepository.save(Reciever);
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(RecieverDTO.getEmail());
        userEntity.setRole( roleRepository.findById(3L).get());
        userEntity.setReciever(Reciever);
        userEntity.setEnabled(RecieverDTO.getStatus());
        userEntity.setPassword(bCryptPasswordEncoder.encode(RecieverDTO.getPassword()));
        userEntityRepository.save(userEntity);
        Reciever.setUserEntity(userEntity);
        return convertToDTO(Reciever);
    }

    public void callReceiver(String massarCode) throws IOException {
        Student student = studentRepository.findByMassarCode(massarCode).orElse(null);
        if (student != null) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setName(student.getName());
            messageDTO.setFormationName(student.getFormation().getName());
            messageDTO.setMassarCode(student.getMassarCode());
            tradeWebSocketHandler.sendMessage(messageDTO);
        }
    }

    public String getCurrentReciever() {
        String email = authService.getCurrentAuthenticatedUser().get().getEmail();
        Reciever reciever = RecieverRepository.findByEmail(email).get();
        return reciever.getFormation().getName();
    }

    public void acceptReciever(MessageDTO messageDTO) {
        StudentLog studentLog = new StudentLog();
        studentLog.setName(messageDTO.getName());
        studentLog.setMassarCode(messageDTO.getMassarCode());
        studentLog.setFormationName(messageDTO.getFormationName());
        studentLogRepository.save(studentLog);
    }
    public void editReceiverPassword(RecieverDTO recieverDTO) {
        UserEntity user = this.userEntityRepository.findByEmail(recieverDTO.getEmail()).get();
        user.setPassword(this.bCryptPasswordEncoder.encode(recieverDTO.getPassword()));
        userEntityRepository.save(user);
    }
}
