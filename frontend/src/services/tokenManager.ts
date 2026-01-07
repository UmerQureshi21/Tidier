// Singleton module to manage access token in memory

let accessToken: string | null = null;

export const tokenManager = {
  setToken(token: string) {
    accessToken = token;
  },

  getToken(): string | null {
    return accessToken;
  },

  clearToken() {
    accessToken = null;
  },

  hasToken(): boolean {
    return accessToken !== null;
  },
};