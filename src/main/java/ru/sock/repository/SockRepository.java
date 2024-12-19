package ru.sock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sock.model.Sock;

import java.util.List;

@Repository
public interface SockRepository extends JpaRepository<Sock, Long> {
    List<Sock> findByColorAndCottonPercentageGreaterThan(String color, int cottonPercentage);
    List<Sock> findByColorAndCottonPercentageLessThan(String color, int cottonPercentage);
    List<Sock> findByColorAndCottonPercentage(String color, int cottonPercentage);
}
