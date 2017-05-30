package me.south10.jpql;

import me.south10.*;
import me.south10.dto.UserDTO;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by spring on 2017-05-27.
 */
public class JPQLExample {
    public static void main(String[] args) {
        // 엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            insert(em);
            selectNamedQuery2(em);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void selectNamedQuery2(EntityManager em) {
        Long result = em.createNamedQuery("Member.count", Long.class)
                .getSingleResult();
        System.out.println("result = " + result);
    }

    private static void selectNamedQuery(EntityManager em) {
        List<Member> resultList = em.createNamedQuery("Member.findByUsename", Member.class)
                .setParameter("username", "김남열").getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectDirectEntity(EntityManager em) {
        String query = "select m from Member m where m = :member";
        Member whereMember = new Member();
        whereMember.setId(3L);
        List<Member> resultList = em.createQuery(query, Member.class)
                .setParameter("member", whereMember).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectCoalesce(EntityManager em) {
        String query = "select coalesce(m.username, '이름없는 회원') from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String str : resultList) {
            System.out.println("str = " + str);
        }
    }

    private static void selectCase2(EntityManager em) {
        String query = "select case t.name when 'NMS개발팀' then '인센티브110%' "
                + "when '인프라개발팀' then '인센티브120%' "
                + "else '인센50%' end FROM Team t";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String str : resultList) {
            System.out.println("str = " + str);
        }
    }

    private static void selectCase1(EntityManager em) {
        String query = "select case when m.age <= 10 then '학생요금' "
                + "when m.age >=60 then '경로요금' "
                + "else '일반요금' end FROM Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String str : resultList) {
            System.out.println("str = " + str);
        }
    }

    private static void selectPrintDate(EntityManager em) {
        String query = "select CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP FROM Member m";
        List<Object[]> resultList = em.createQuery(query).getResultList();
        for (Object[] row : resultList) {
            Date date = (Date) row[0];
            Time time = (Time) row[1];
            Timestamp timestamp = (Timestamp) row[2];
            System.out.println("date = " + date + ", time = " + time +", timestamp = " + timestamp);
        }
    }

    private static void selectWhereCollection(EntityManager em) {
        String query = "select m from Member m " + "where m.orders is not empty";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectWhereNull(EntityManager em) {
        String query = "select m from Member m " + "where m.username is not null";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectWhereLike(EntityManager em) {
        String query = "select m from Member m " + "where m.username like '김남_'";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectWhereIn(EntityManager em) {
        String query = "select m from Member m " +
                "where m.username in('김남열','정기정')";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectWhereBetween(EntityManager em) {
        String query = "select m from Member m " +
                "where m.age between 20 and 30";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectWhereSub1(EntityManager em) {
        //String query = "select m from Member m " + "where m.age > (select avg(m2.age) from Member m2)";
        //String query = "select m from Member m " + "where exists (select t from m.team t where t.name='NMS개발팀')";
        String query = "select m from Member m " +
                "where m.team = ANY (select t from m.team t)";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectPathExpression1(EntityManager em) {
        String query = "select o.member.team " +
                        "from Order o " +
                        "where o.product.name = '상품1' and o.address.city='seoul'";

        List<Team> resultList = em.createQuery(query, Team.class).getResultList();
        for (Team team : resultList) {
            System.out.println("team = " + team);
        }
    }

    private static void selectFetchJoin2(EntityManager em) {
        String query = "select  t from Team t join fetch t.members where t.name = :teamName";
        List<Team> resultList = em.createQuery(query, Team.class).setParameter("teamName", "NMS개발팀")
                .getResultList();
        for (Team team : resultList) {
            System.out.println("teamanem = " + team.getName() + ", team = " + team);

            for (Member member : team.getMembers()) {
                System.out.println("->username = " + member.getUsername() +", member = " + member);
            }
        }
    }

    private static void selectFetchJoin(EntityManager em) {
        String query = "select m from Member m join fetch m.team";

        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        for (Member member : resultList) {
            System.out.println("username = " + member.getUsername() +", " +
                            "teamname = " + member.getTeam().getName());
        }
    }

    private static void selectLeftJoinOn(EntityManager em) {
        String query = "SELECT m, t FROM Member m " +
                        "LEFT JOIN m.team t ON t.name = :teamName";

        List<Object[]> resultList = em.createQuery(query).setParameter("teamName", "인프라개발팀")
                .getResultList();

        for (Object[] row : resultList) {
            Team team = (Team) row[1];
            Member member = (Member) row[0];
            System.out.println("member = " + member + ", Team = " + team);
        }
    }

    private static void selectLeftJoin1(EntityManager em) {
        String query = "SELECT t, m FROM Team t LEFT JOIN t.members m";
        //예전 EJB 방식
        //String query = "SELECT t, m FROM Team t, IN(t.members) m";


        List<Object[]> resultList = em.createQuery(query).getResultList();
        for (Object[] row : resultList) {
            Team team = (Team) row[0];
            Member member = (Member) row[1];
            System.out.println("member = " + member + ", Team = " + team);
        }
    }

    private static void selectInnerJoin2(EntityManager em) {
        String teamName = "NMS개발팀";
        String query = "SELECT m, t FROM Member m INNER JOIN m.team t "
                + "WHERE t.name = :teamName";
        List<Object[]> result = em.createQuery(query).setParameter("teamName", teamName)
                .getResultList();

        for (Object[] row: result) {
            Member member = (Member) row[0];
            Team team = (Team) row[1];
            System.out.println("member = " + member + ", Team = " + team);
        }
    }

    private static void selectInnerJoin1(EntityManager em) {
        String teamName = "NMS개발팀";
        String query = "SELECT m FROM Member m INNER JOIN m.team t "
                        + "WHERE t.name = :teamName";
        List<Member> members = em.createQuery(query, Member.class).setParameter("teamName", teamName)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    private static void selectPaging(EntityManager em) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m ORDER BY m.username DESC", Member.class);

        query.setFirstResult(0);
        query.setMaxResults(3);
        List<Member> resultList = query.getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void selectProjectionByScalaDtoNew(EntityManager em) {
        List<UserDTO> resultList = em.createQuery("SELECT new me.south10.dto.UserDTO(m.username, m.age) FROM Member m")
                .getResultList();

        for (UserDTO userDTO : resultList) {
            System.out.println("userDTO = " + userDTO);
        }
    }

    private static void selectProjectionByScalaDto(EntityManager em) {
        List<Object[]> resultList = em.createQuery("SELECT m.username, m.age FROM Member m")
                .getResultList();

        List<UserDTO> userDTOs = new ArrayList<>();
        for (Object[] row : resultList) {
            UserDTO userDTO = new UserDTO((String)row[0], (int)row[1]);
            userDTOs.add(userDTO);
            System.out.println(userDTO);
        }
    }

    private static void selectProjectionByScalaMultiEntity(EntityManager em) {
        List<Object[]> resultList = em.createQuery("SELECT o.member, o.product, o.orderAmount FROM Order o")
                .getResultList();

        for (Object[] row : resultList) {
            Member member = (Member) row[0];
            Product product = (Product) row[1];
            int orderAmount = (int) row[2];
            System.out.println(member + ", " + product + ", " + orderAmount);
        }
    }

    private static void selectProjectionByScalaMultiValue(EntityManager em) {
        String query = "SELECT m.username, m.age FROM Member m";
        List<Object[]> resultList = em.createQuery(query).getResultList();

        for (Object[] row : resultList) {
            String username = (String) row[0];
            int age = (int) row[1];
            System.out.println(username + ", " + age);
        }
    }

    private static void selectProjectionByScala2(EntityManager em) {
        String query = "SELECT AVG(o.orderAmount) FROM Order o";
        Double orderAmountAvg = em.createQuery(query, Double.class)
                .getSingleResult();
        System.out.println("orderAmountAvg = " + orderAmountAvg);
    }

    private static void selectProjectionByScala(EntityManager em) {
        String query = "SELECT m.username FROM Member m";
        List<String> usernames = em.createQuery(query, String.class).getResultList();
        for (String username : usernames) {
            System.out.println("username = " + username);
        }

    }

    private static void selectProjection(EntityManager em) {
        String query = "SELECT o.address FROM Order o";
        List<Address> addresses = em.createQuery(query, Address.class)
                .getResultList();

        for (Address address : addresses) {
            System.out.println("address = " + address);
        }
    }

    private static void useParameterBindingByLocation(EntityManager em) {
        List<Member> members = em.createQuery("SELECT m FROM Member m where m.username = ?1",
                Member.class).setParameter(1, "정기정")
                .getResultList();
        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    private static void useParameterBinding(EntityManager em) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m where m.username = :username",
                Member.class).setParameter("username", "김남열");
        List<Member> resultList = query.getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void useQuery(EntityManager em) {
        Query query = em.createQuery("SELECT m.username, m.age FROM Member m");
        List resultList = query.getResultList();

        for (Object o : resultList) {
            Object[] result = (Object[]) o;
            System.out.println("username = " + result[0]);
            System.out.println("age = " + result[1]);
        }
    }

    private static void logic(EntityManager em) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);

        List<Member> resultList = query.getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    public static void insert(EntityManager em) {
        Team team1 = new Team("NMS개발팀");
        Team team2 = new Team("인프라개발팀");
        Member member1 = new Member("김남열", 32);
        Member member2 = new Member("정기정", 35);
        member1.setTeam(team1);
        member2.setTeam(team1);
        Member member3 = new Member("홍지은", 28);
        Member member4 = new Member("김혜리", 31);
        member3.setTeam(team1);
        member4.setTeam(team2);
        em.persist(team1);
        em.persist(team2);

        Product product1 = new Product("상품1", 30000, 5);
        Product product2 = new Product("상품2", 20000, 3);

        Order order1 = new Order();
        order1.setAddress(new Address("seoul","dobong","132045"));
        order1.setOrderAmount(3);
        order1.setMember(member1);
        order1.setProduct(product1);

        em.persist(product1);
        em.persist(product2);

    }

}
