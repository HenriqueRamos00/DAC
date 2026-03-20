import { createContext, useContext, type ReactNode } from "react"
import type { AuthUser } from "~/models/auth/AuthUser"

type AuthContextValue = {
    auth: AuthUser
    isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextValue | null> (null)

export function AuthProvider({
  initialAuth,
  children,
}: {
  initialAuth: AuthUser
  children: ReactNode
}) {
  return (
    <AuthContext.Provider
      value={{
        auth: initialAuth,
        isAuthenticated: Boolean(initialAuth.token),
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error("useAuth deve ser usado dentro do AuthProvider")
  }

  return context
}