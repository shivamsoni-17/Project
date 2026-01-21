import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  userId: number;
  username: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth';
  private readonly storageKey = 'hms-auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { username, password })
      .pipe(tap(res => this.setSession(res)));
  }

  register(user: any) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, user);
  }

  setSession(res: AuthResponse) {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(this.storageKey, JSON.stringify(res));
    }
  }

  logout() {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.storageKey);
    }
  }

  getToken(): string | null {
    const data = this.getSession();
    return data?.token || null;
  }

  getSession(): AuthResponse | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }
    const raw = localStorage.getItem(this.storageKey);
    return raw ? JSON.parse(raw) as AuthResponse : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
