package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

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

    // con, stmt 동시에 닫기 위해 필요. (finally에서 개별로 각각 닫으면 앞에서 에러 터질 시 뒷 close 실행 안됨)
    private void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}