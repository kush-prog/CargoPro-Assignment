package com.kush.cargoProAssignment.service;

import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.repository.LoadRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LoadService {

    private final LoadRepository loadRepository;
    private final ModelMapper modelMapper;

    public LoadDTO createLoad(LoadDTO loadDTO) {
        Load load = modelMapper.map(loadDTO, Load.class);
        load.setStatus(LoadStatus.POSTED);
        Load savedLoad = loadRepository.save(load);
        return modelMapper.map(savedLoad, LoadDTO.class);
    }

    public Page<LoadDTO> getLoads(String shipperId, String truckType, LoadStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return loadRepository.findByFilters(shipperId, truckType, status, pageable)
                .map(load -> modelMapper.map(load, LoadDTO.class));
    }

    public LoadDTO getLoadById(UUID id) {
        Load load = findEntityById(id);
        return modelMapper.map(load, LoadDTO.class);
    }

    public LoadDTO updateLoad(UUID id, LoadDTO loadDTO) {
        Load existingLoad = findEntityById(id);

        // Update allowed fields (ID is auto-managed)
        existingLoad.setShipperId(loadDTO.getShipperId());
        existingLoad.setFacility(modelMapper.map(loadDTO.getFacility(), existingLoad.getFacility().getClass()));
        existingLoad.setProductType(loadDTO.getProductType());
        existingLoad.setTruckType(loadDTO.getTruckType());
        existingLoad.setNoOfTrucks(loadDTO.getNoOfTrucks());
        existingLoad.setWeight(loadDTO.getWeight());
        existingLoad.setComment(loadDTO.getComment());

        Load updatedLoad = loadRepository.save(existingLoad);
        return modelMapper.map(updatedLoad, LoadDTO.class);
    }

    public void deleteLoad(UUID id) {
        Load load = findEntityById(id);
        loadRepository.delete(load);
    }

    public Load findEntityById(UUID id) {
        return loadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + id));
    }

    public void updateLoadStatus(UUID loadId, LoadStatus status) {
        Load load = findEntityById(loadId);
        load.setStatus(status);
        loadRepository.save(load);
    }
}
