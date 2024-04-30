package com.project.tklembackend.service.admin;

import com.project.tklembackend.dto.ParentDTO;
import com.project.tklembackend.dto.ReceptorDTO;
import com.project.tklembackend.model.Receptor;
import com.project.tklembackend.model.UserEntity;
import com.project.tklembackend.repository.ReceptorRepository;
import com.project.tklembackend.repository.RoleRepository;
import com.project.tklembackend.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AdminReceptorService {
    private final ReceptorRepository receptorRepository;
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Transactional
    public List<ReceptorDTO> getAllReceptors(){
        return receptorRepository.findAll().stream().map(this::convertToDTO).toList();
    }
    @Transactional
    public ReceptorDTO addReceptor(ReceptorDTO receptorDTO) {
            return buildReceptor(receptorDTO);
    }
    @Transactional
    public ReceptorDTO editReceptor(ReceptorDTO receptorDTO) {
        try {
            if (receptorRepository.existsById(receptorDTO.getId())) {
                Receptor existingReceptor = receptorRepository.findById(receptorDTO.getId()).get();
                updateReceptorFromDTO(existingReceptor, receptorDTO);
                receptorRepository.save(existingReceptor);
                return convertToDTO(existingReceptor);
            } else {
                throw new NoSuchElementException("Receptor you are trying to edit doesn't exist");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void updateReceptorFromDTO(Receptor receptor, ReceptorDTO receptorDTO) {
        receptor.setName(receptorDTO.getName());
        UserEntity userEntity = receptor.getUserEntity();
        userEntity.setEmail(receptorDTO.getEmail());
        userEntity.setEnabled(receptorDTO.getStatus());
        userEntity.setPassword(bCryptPasswordEncoder.encode(receptorDTO.getPassword()));
        userEntityRepository.save(userEntity);

    }
    @Transactional
    public void deleteReceptors(Long id){
        UserEntity userEntity = receptorRepository.findById(id).get().getUserEntity();
        userEntityRepository.delete(userEntity);
        receptorRepository.deleteById(id);
    }

    private ReceptorDTO convertToDTO(Receptor receptor) {
        ReceptorDTO receptorDTO = modelMapper.map(receptor, ReceptorDTO.class);
        receptorDTO.setEmail(receptor.getEmail());
        receptorDTO.setPassword(receptor.getUserEntity().getPassword());
        receptorDTO.setStatus(receptor.getUserEntity().getEnabled());
        return receptorDTO;
    }

    private ReceptorDTO buildReceptor(ReceptorDTO receptorDTO){
        Receptor receptor = new Receptor();
        if(receptorDTO.getId() != null){
            receptor.setId(receptor.getId());
        }
        receptor.setName(receptorDTO.getName());
        receptor.setEmail(receptorDTO.getEmail());
        receptorRepository.save(receptor);
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(receptorDTO.getEmail());
        userEntity.setRole( roleRepository.findById(2L).get());
        userEntity.setReceptor(receptor);
        userEntity.setEnabled(receptorDTO.getStatus());
      //  userEntity.setPassword(bCryptPasswordEncoder.encode(receptorDTO.getPassword()));
        userEntityRepository.save(userEntity);
        receptor.setUserEntity(userEntity);
        return convertToDTO(receptor);
    }
    public void editReceptorPassword(ReceptorDTO receptorDTO) {
        UserEntity user = this.userEntityRepository.findByEmail(receptorDTO.getEmail()).get();
        user.setPassword(this.bCryptPasswordEncoder.encode(receptorDTO.getPassword()));
        userEntityRepository.save(user);
    }

}
