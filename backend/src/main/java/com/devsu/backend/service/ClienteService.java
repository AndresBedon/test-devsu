package com.devsu.backend.service;


import com.devsu.backend.model.Cliente;
import com.devsu.backend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import com.devsu.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente cliente) {

        Cliente existing = this.findById(id);
        existing.setNombre(cliente.getNombre());
        existing.setGenero(cliente.getGenero());
        existing.setEdad(cliente.getEdad());
        existing.setDireccion(cliente.getDireccion());
        existing.setTelefono(cliente.getTelefono());
        existing.setContrasena( cliente.getContrasena());
        existing.setEstado(cliente.getEstado());
        return clienteRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        clienteRepository.deleteById(id);
    }
}
