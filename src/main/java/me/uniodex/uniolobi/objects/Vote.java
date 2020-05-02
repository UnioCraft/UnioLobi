package me.uniodex.uniolobi.objects;

public class Vote {

	public Long lastVotedTime_site1;
	public Long lastVotedTime_site2;
	public Long lastVotedTime_site3;
	public Long lastVotedTime_site4;

	public Vote(Long lastVotedTime_site1, Long lastVotedTime_site2, Long lastVotedTime_site3, Long lastVotedTime_site4) {
		this.lastVotedTime_site1 = lastVotedTime_site1;
		this.lastVotedTime_site2 = lastVotedTime_site2;
		this.lastVotedTime_site3 = lastVotedTime_site3;
		this.lastVotedTime_site4 = lastVotedTime_site4;
	}
}
