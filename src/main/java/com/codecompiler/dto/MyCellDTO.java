package com.codecompiler.dto;

public class MyCellDTO {

	private String content;

    public MyCellDTO() {
    }

    public MyCellDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

	@Override
	public String toString() {
		return "MyCell [content=" + content + "]";
	}
}
