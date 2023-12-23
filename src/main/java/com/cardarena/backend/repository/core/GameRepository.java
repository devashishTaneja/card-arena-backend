package com.cardarena.backend.repository.core;

import com.cardarena.backend.models.core.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}
