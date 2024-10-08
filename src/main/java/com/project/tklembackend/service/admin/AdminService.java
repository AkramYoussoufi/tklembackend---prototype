package com.project.tklembackend.service.admin;

import com.project.tklembackend.dto.UserDTO;
import com.project.tklembackend.model.Roles;
import com.project.tklembackend.model.UserEntity;
import com.project.tklembackend.repository.UserEntityRepository;
import com.project.tklembackend.service.AuthService;
import com.project.tklembackend.service.GlobalService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserEntityRepository userEntityRepository;
    private final GlobalService globalService;
    private final AuthService authService;


    @Transactional
    public UserDTO addAdmin(UserDTO userDTO) throws InstanceAlreadyExistsException {
        if(!userEntityRepository.existsByEmail(userDTO.getEmail())){
            UserEntity user = globalService.adminDTOtoAdmin(userDTO);
            userEntityRepository.save(user);
            return globalService.adminToAdminDTO(user);
        }else{
            throw new InstanceAlreadyExistsException("Instance you trying to add already exist");
        }
    }

    @Transactional
    public UserDTO editAdmin(UserDTO userDTO) throws NoSuchElementException {
        if(userEntityRepository.existsById(userDTO.getId())){
            UserEntity user = globalService.adminDTOtoAdmin(userDTO);
            userEntityRepository.save(user);
            return globalService.adminToAdminDTO(user);
        }else{
            throw new NoSuchElementException("Instance you trying to modify doesn't exist");
        }
    }
    public void deleteAdmin(Long id) {
        if(Objects.equals(userEntityRepository.findByEmail("admin").get().getId(), id) || Objects.equals(authService.getCurrentAuthenticatedUser().get().getId(), id)){
            throw new RequestRejectedException("You cannot delete your account or main Admin account");
        }
        userEntityRepository.deleteById(id);
    }



    public List<UserDTO> getAllAdmin() {
        return userEntityRepository.findAll().stream().filter(
                userEntity -> {
                    return userEntity.getRole().getRoleName() == Roles.ADMIN;
                }
        ).map(globalService::adminToAdminDTO).toList();
    }
}
