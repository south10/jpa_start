package me.south10.jpql;

import me.south10.Member;
//import me.south10.Member_;
import me.south10.Team;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.List;

public class CriteriaExample {
    public static void main(String[] args) {
        // 엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            JPQLExample.insert(em);
            selectMemberParameter(em);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    /*private static void selectMemberMetaModelApi(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        Root<Member> m = cq.from(Member.class);
        cq.select(m)
                .where(cb.gt(m.get(Member_.age), 20))
                .orderBy(cb.desc(m.get(Member_.age)));

        List<Member> resultList = em.createQuery(cq).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }*/

    private static void selectMemberParameter(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        Root<Member> m = cq.from(Member.class);

        cq.select(m)
                .where(cb.equal(m.get("username"), cb.parameter(String.class, "usernameParam")));

        List<Member> resultList = em.createQuery(cq)
                .setParameter("usernameParam", "김남열")
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectMemberCase(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<Member> m = cq.from(Member.class);
        cq.multiselect(m.get("username"),
                cb.selectCase()
                .when(cb.ge(m.<Integer>get("age"), 30), 600)
                .when(cb.le(m.<Integer>get("age"), 15), 500)
                .otherwise(1000));

        List<Object[]> resultList = em.createQuery(cq).getResultList();
        for (Object[] row : resultList) {
            String username = (String) row[0];
            int caseValue = (int) row[1];
            System.out.println("username = " + username + ",caseValue = " + caseValue);
        }

    }

    private static void selectMemberInCondition(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);
        Root<Member> m = cq.from(Member.class);

        cq.select(m)
                .where(cb.in(m.get("username"))
                .value("김남열")
                .value("정기정"));

        List<Member> resultList = em.createQuery(cq).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectMemberRelatedSubQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> mainQuery = cb.createQuery(Member.class);

        Root<Member> m = mainQuery.from(Member.class);

        Subquery<Team> subquery = mainQuery.subquery(Team.class);
        Root<Member> subM = subquery.correlate(m);
        Join<Member, Team> t = subM.join("team");
        subquery.select(t)
                .where(cb.equal(t.get("name"), "NMS개발팀"));

        mainQuery.select(m)
                .where(cb.exists(subquery));

        List<Member> resultList = em.createQuery(mainQuery).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }

    }

    private static void selectMemberSubQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> mainQuery = cb.createQuery(Member.class);

        Subquery<Double> subquery = mainQuery.subquery(Double.class);

        Root<Member> m2 = subquery.from(Member.class);
        subquery.select(cb.avg(m2.<Integer>get("age")));

        Root<Member> m = mainQuery.from(Member.class);
        mainQuery.select(m)
                .where(cb.ge(m.<Integer>get("age"), subquery));

        List<Member> resultList = em.createQuery(mainQuery).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectMemberFetchJoin(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        Root<Member> m = cq.from(Member.class);
        m.fetch("team", JoinType.LEFT);

        cq.select(m);

        List<Member> resultList = em.createQuery(cq).getResultList();
        for (Member member: resultList) {
            System.out.println("member = " + member + "team = " + member.getTeam().getName());
        }
    }

    private static void selectMemberJoin(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<Member> m = cq.from(Member.class);
        Join<Member, Team> t = m.join("team", JoinType.INNER);

        cq.multiselect(m, t)
                .where(cb.equal(t.get("name"), "NMS개발팀"));

        List<Object[]> resultList = em.createQuery(cq).getResultList();
        for (Object[] row : resultList) {
            Member member = (Member) row[0];
            Team team = (Team) row[1];
            System.out.println("team = " + team + ", member = " + member);
        }
    }

    private static void selectMemberGroupBy(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Member> m = cq.from(Member.class);

        Expression<Integer> maxAge = cb.max(m.<Integer>get("age"));
        Expression<Integer> minAge = cb.min(m.<Integer>get("age"));

        cq.multiselect(m.get("team").get("name"), maxAge, minAge);
        cq.groupBy(m.get("team").get("name"))
                .having(cb.gt(minAge, 10))
                .orderBy(cb.desc(m.get("team").get("name")));

        TypedQuery<Object[]> query = em.createQuery(cq);
        List<Object[]> resultList = query.getResultList();
        for (Object[] row : resultList) {
            String name = (String)row[0];
            int max = (int) row[1];
            int min = (int) row[2];
            System.out.println("name = " + name + ", max = " + max + ", min = " + min);
        }
    }

    private static void selectMemberTuple(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Member> m = cq.from(Member.class);

        cq.select(cb.tuple(
                m.alias("m"),
                m.get("username").alias("username")
        ));

        TypedQuery<Tuple> query = em.createQuery(cq);
        List<Tuple> resultList = query.getResultList();
        for (Tuple tuple : resultList) {
            Member member = tuple.get("m", Member.class);
            String username = tuple.get("username", String.class);

            System.out.println("member = " + member + ", username = " + username);
        }

    }

    private static void selectMemberMultiSelect(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Member> m = cq.from(Member.class);
        //cq.multiselect(m.get("username"), m.get("age")).distinct(true);
        cq.select(cb.array(m.get("username"), m.get("age"))).distinct(true);

        List<Object[]> resultList = em.createQuery(cq).getResultList();
        for (Object[] row : resultList) {
            String username = (String)row[0];
            int age = (int) row[1];
            System.out.println("username = " + username + ", age = " + age);
        }
    }

    private static void selectMemberByAge(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        Root<Member> m = cq.from(Member.class);

        Predicate ageGt = cb.greaterThan(m.<Integer>get("age"), 10);

        cq.select(m);
        cq.where(ageGt);
        cq.orderBy(cb.desc(m.get("age")));

        List<Member> resultList = em.createQuery(cq).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }

    }

    private static void selectCriteria(EntityManager em) {
        // Criteria 쿼리 빌더
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Criteria 생성, 반환타입 지정
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        // from 절
        Root<Member> m = cq.from(Member.class);

        // 검색 조건 정의
        Predicate usernameEqual = cb.equal(m.get("username"), "김남열");

        // 정렬 조건 정의
        Order ageDesc = cb.desc(m.get("age"));

        // 쿼리 생성
        cq.select(m)
                .where(usernameEqual)
                .orderBy(ageDesc);


        TypedQuery<Member> query = em.createQuery(cq);
        List<Member> resultList = query.getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }


}
