package me.south10.jpql;

import com.mysema.query.jpa.impl.JPAQuery;
import me.south10.Member;
import me.south10.Product;
import me.south10.QMember;
import me.south10.QProduct;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

import static me.south10.QMember.member;
/**
 * Created by spring on 2017-05-28.
 */
public class QueryDSLExample {
    public static void main(String[] args) {
        // 엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            JPQLExample.insert(em);
            queryDSLCondition(em);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void queryDSLCondition(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QProduct product = QProduct.product;
        List<Product> list = query.from(product)
                .where(product.name.eq("상품1").and(product.price.gt(20000)))
                .list(product);

        for (Product product1 : list) {
            System.out.println("product1 = " + product1);
        }
    }

    private static void queryDSLInstance(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        List<Member> members = query.from(member)
                .where(member.username.eq("정기정"))
                .orderBy(member.username.desc())
                .list(member);

        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    private static void queryDSL(EntityManager em) {
        JPAQuery query = new JPAQuery(em);
        QMember qMember = new QMember("m");
        List<Member> members = query.from(qMember)
                .where(qMember.username.eq("김남열"))
                .orderBy(qMember.username.desc())
                .list(qMember);

        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }
}
