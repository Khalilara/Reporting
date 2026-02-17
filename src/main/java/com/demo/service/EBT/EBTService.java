package com.demo.service.EBT;

import com.demo.Model.EBT.TableauEBT;
import com.demo.Repository.EBT.TableauEbtRepository;
import com.demo.Repository.EBT.EvolutionEBTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EBTService {

    @Autowired
    TableauEbtRepository tableauEbtRepository;
    @Autowired
    EvolutionEBTRepository evolutionEBTRepository;

}
