package me.south10;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import me.south10.dto.UserDTO;

/**
 * Created by spring on 2017-05-14.
 */
public class JpaMain {
    public static void main(String[] args) {
        // 엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

}
