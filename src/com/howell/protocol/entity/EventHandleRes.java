package com.howell.protocol.entity;



public class EventHandleRes {
	int message;
	int cseq;
	Notice notice;
	public EventHandleRes() {
		super();
	}
	public EventHandleRes(int message, int cseq, Notice notice) {
		super();
		this.message = message;
		this.cseq = cseq;
		this.notice = notice;
	}
	public int getMessage() {
		return message;
	}
	public void setMessage(int message) {
		this.message = message;
	}
	public int getCseq() {
		return cseq;
	}
	public void setCseq(int cseq) {
		this.cseq = cseq;
	}
	public Notice getNotice() {
		return notice;
	}
	public void setNotice(Notice notice) {
		this.notice = notice;
	}
	public static class Notice{
		String id;
		String message;
		String classification;
		String time;
		String status;
		String sender;
		String componentID;
		String componentName;
		String NoticeType;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getClassification() {
			return classification;
		}
		public void setClassification(String classification) {
			this.classification = classification;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getComponentID() {
			return componentID;
		}
		public void setComponentID(String componentID) {
			this.componentID = componentID;
		}
		public String getComponentName() {
			return componentName;
		}
		public void setComponentName(String componentName) {
			this.componentName = componentName;
		}
		public String getNoticeType() {
			return NoticeType;
		}
		public void setNoticeType(String noticeType) {
			NoticeType = noticeType;
		}
		public  Notice(String id, String message, String classification, String time, String status, String sender,
				String componentID, String componentName, String noticeType) {
			super();
			this.id = id;
			this.message = message;
			this.classification = classification;
			this.time = time;
			this.status = status;
			this.sender = sender;
			this.componentID = componentID;
			this.componentName = componentName;
			NoticeType = noticeType;
		}
		public Notice() {
			super();
		}
		
	}
}
