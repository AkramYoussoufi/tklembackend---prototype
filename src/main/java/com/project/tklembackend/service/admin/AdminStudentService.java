package com.project.tklembackend.service.admin;

import com.project.tklembackend.dto.StudentDTO;
import com.project.tklembackend.model.Formation;
import com.project.tklembackend.model.Parent;
import com.project.tklembackend.model.Student;
import com.project.tklembackend.repository.FormationRepository;
import com.project.tklembackend.repository.ParentRepository;
import com.project.tklembackend.repository.StudentRepository;
import com.project.tklembackend.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AdminStudentService {
    private final StudentRepository studentRepository;
    private final FormationRepository formationRepository;
    private final AuthService authService;
    private ParentRepository parentRepository;

    @Transactional
    public List<StudentDTO> getAllStudent(){
        ArrayList<StudentDTO> studentDTOArrayList = new ArrayList<>();
        studentRepository.findAll().forEach(
                student -> {
                    StudentDTO response = new StudentDTO();
                    response.setId(student.getId());
                    response.setName(student.getName());
                    response.setFormationName(student.getFormation().getName());
                    response.setMassarCode(student.getMassarCode());
                    studentDTOArrayList.add(response);
                }
        );
        return studentDTOArrayList;
    }

    @Transactional
    public Student addStudent(StudentDTO studentDTO) throws InstanceAlreadyExistsException {
        if(!studentRepository.existsByMassarCode(studentDTO.getMassarCode())){
            Student student = new Student();
            student.setName(studentDTO.getName());
            student.setMassarCode(studentDTO.getMassarCode());
            Formation formation = formationRepository.findByName(studentDTO.getFormationName()).orElseThrow(
                    () -> new NoSuchElementException("Student formation doesn't exist")
            );
            student.setFormation(formation);
            return studentRepository.save(student);
        }
        throw new InstanceAlreadyExistsException("Massar Code of this student already exist");
    }

    @Transactional
    public Student editStudent(StudentDTO studentDTO) {
        if(studentRepository.existsById(studentDTO.getId())){
            Student student = StudentDTOtoStudent(studentDTO);
            studentRepository.save(student);
            return student;
        }
        throw new NoSuchElementException("element you trying to modify doesn't exist");
    }

    @Transactional
    public void deleteStudent(Long id) {
        try{
            studentRepository.delete(studentRepository.findById(id).get());
        }catch (Exception e){
            throw new NoSuchElementException("element you trying to delete doesn't exist");
        }
    }

    @Transactional
    public void deleteAllStudent(ArrayList<Long> response) {
        try{
            if(response.size() == studentRepository.count()){
                studentRepository.deleteAll();
            }else{
                studentRepository.deleteAllById(response);
            }
        }catch (Exception e){
            throw new NoSuchElementException("element you trying to delete doesn't exist");
        }
    }

    private Student StudentDTOtoStudent(StudentDTO studentDTO){
        Student student = new Student();
        if(studentDTO.getId() != null){
            student.setId(studentDTO.getId());
        }
        student.setName(studentDTO.getName());
        Formation formation = formationRepository.findByName(studentDTO.getFormationName()).orElseThrow(
                () -> new NoSuchElementException("Student formation doesn't exist")
        );
        student.setFormation(formation);
        student.setMassarCode(studentDTO.getMassarCode());

        studentRepository.save(student);
        return student;
    }


    public List<StudentDTO> getMyStudents() {
        Parent parent =  parentRepository.findByEmail(this.authService.getCurrentAuthenticatedUser().get().getEmail()).get();
        return parent.getStudents().stream().map(item->{
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setName(item.getName());
            studentDTO.setMassarCode(item.getMassarCode());
            studentDTO.setFormationName(item.getFormation().getName());
            return  studentDTO;
        }).toList();
    }

    public void deleteStudentFromParent(String massarCode) {
        // Retrieve the student
        System.out.println(massarCode);
        Student student = studentRepository.findByMassarCode(massarCode)
                .orElseThrow(() -> new NoSuchElementException("Student not found"));

// Retrieve the parent
        Parent parent = parentRepository.findByEmail(authService.getCurrentAuthenticatedUser().get().getEmail())
                .orElseThrow(() -> new NoSuchElementException("Parent not found"));

// Remove the parent from the list of parents associated with the student
        student.getParents().remove(parent);
        studentRepository.save(student);
    }

    public void addAllStudent(List<StudentDTO> studentDTOList) {
        List<Student> data = studentDTOList.stream().map(
                studentDTO -> {
                    if(!formationRepository.existsByName(studentDTO.getFormationName())){
                        Formation formation = new Formation();
                        formation.setName(studentDTO.getFormationName());
                       this.formationRepository.save(formation);
                    }
                    if(studentRepository.existsByMassarCode(studentDTO.getMassarCode())){
                        studentDTO.setId(studentRepository.findByMassarCode(studentDTO.getMassarCode()).get().getId());
                    }
                    return this.StudentDTOtoStudent(studentDTO);
                }
        ).toList();
        studentRepository.saveAll(data);
    }

}
