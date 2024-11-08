package org.example.mock.Service;

import org.example.mock.Model.PositionAll;
import org.example.mock.Repository.PositionRepository;

import javax.swing.text.Position;
import java.util.List;

public class PositionService {
    private PositionRepository positionRepository;

    public List<PositionAll> getAllPositions() {
        return positionRepository.findAll();
    }
}
