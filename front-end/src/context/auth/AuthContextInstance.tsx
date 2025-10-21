import type { AuthContextType } from './authUtils';
import { createContext } from 'react';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);
