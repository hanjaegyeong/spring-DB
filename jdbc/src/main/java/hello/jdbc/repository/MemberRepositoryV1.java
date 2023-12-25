package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager, JdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)"; //파라미터 바인딩 위해 물음표 이용 (?, ?)

        Connection con = null;
        PreparedStatement pstmt = null; //PreparedStatement 반드시 사용: 파라미터 바인딩 가능

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql); //DB에 전달할 SQL 세팅
            //DB에 파라미터로 전달할 데이터들 세팅
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); //준비된 쿼리가 실제 DB에 실행
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); //항상 커넥션 열면 닫아줘야 함
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null; //ResultSet 선언: select 쿼리의 결과를 담고 있는 통

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery(); // select 쿼리 결과 받아오기
            
            if (rs.next()) { // rs.next() 최소 한 번은 실행해야 데이터 조회됨(최초 커서는 데이터 안가리킴)
                Member member = new Member();
                member.setMemberId(rs.getString("member_id")); //rs에서 데이터들 불러와서
                member.setMoney(rs.getInt("money"));
                return member; //member객체 반환
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con); //커넥션풀의 close는 커넥션 아예 닫는게 아니라 커넥션풀로 반환하는 것
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection(); //커넥션 시 dataSource 사용
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}