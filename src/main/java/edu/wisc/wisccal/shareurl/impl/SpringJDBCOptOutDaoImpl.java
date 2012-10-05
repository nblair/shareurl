/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nicholas Blair
 */
@Transactional
@Repository
class SpringJDBCOptOutDaoImpl implements OptOutDao {

	static final String N = "N";
	static final String Y = "Y";
	private SimpleJdbcTemplate simpleJdbcTemplate;
	/**
	 * 
	 * @param dataSource
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/**
	 * @return the simpleJdbcTemplate
	 */
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.impl.OptOutDao#optOut(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void optOut(ICalendarAccount account) {
		if(!isOptOut(account)) {
			this.simpleJdbcTemplate.update("insert into opt_out (account, val, effective) values (?, ?, ?)",
					account.getCalendarUniqueId(), Y, new Date());
		}
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.impl.OptOutDao#optIn(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void optIn(ICalendarAccount account) {
		if(isOptOut(account)) {
			this.simpleJdbcTemplate.update("insert into opt_out (account, val, effective) values (?, ?, ?)",
					account.getCalendarUniqueId(), N, new Date());
		}
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.impl.OptOutDao#isOptOut(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public boolean isOptOut(ICalendarAccount account) {
		List<OptOutRecord> records = getOptOutRecords(account);
		OptOutRecord effective = findEffective(records);
		return effective != null && Y.equals(effective.getValue());
	}

	/**
	 * 
	 * @param records
	 * @return
	 */
	protected OptOutRecord findEffective(List<OptOutRecord> records) {
		if(records == null || records.isEmpty()) {
			return null;
		}
		
		if(records.size() == 1) {
			return records.get(0);
		}
		// compareTo for OptOutRecord sorts descending by timestamp
		Collections.sort(records);
		return records.get(0);
	}
	/**
	 * 
	 * @param account
	 * @return
	 */
	protected List<OptOutRecord> getOptOutRecords(ICalendarAccount account) {
		List<OptOutRecord> results = this.getSimpleJdbcTemplate().query("select * from opt_out where account=?",
				new RowMapper<OptOutRecord>() {
					@Override
					public OptOutRecord mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						OptOutRecord result = new OptOutRecord();
						result.setAccount(rs.getString("ACCOUNT"));
						result.setTimestamp(rs.getTimestamp("EFFECTIVE"));
						result.setValue(rs.getString("VAL"));
						return result;
					}
				}, account.getCalendarUniqueId());
		return results;
	}
}
