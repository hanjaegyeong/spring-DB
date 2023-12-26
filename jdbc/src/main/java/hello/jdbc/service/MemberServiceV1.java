package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    // 송금 로직
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId); //돈 보낼 멤버
        Member toMember = memberRepository.findById(toId); //돈 받을 멤버
        memberRepository.update(fromId, fromMember.getMoney() - money); //money만큼 송금
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    //username이 "ex" ->  예외발생
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}