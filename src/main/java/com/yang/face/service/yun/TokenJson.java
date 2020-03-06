package com.yang.face.service.yun;

/*
 * yangyuyang 2018-12-13
 * 生成token
 * */
public class TokenJson {
	
	private int error;
	
	private Result data;
	
	public class Result {
		
		private int result;
		
		private String token;
		

		public int getResult() {
			return result;
		}

		public void setResult(int result) {
			this.result = result;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
		
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Result getData() {
		return data;
	}

	public void setData(Result data) {
		this.data = data;
	}
		
}


