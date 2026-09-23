import { createContext } from 'react'

/** Valore: { sessione, login, register, logout, isAdmin }. Popolato da AuthProvider. */
export const AuthContext = createContext(null)
