import { useContext } from 'react';
import { AuthContext } from './AuthContextInstance';

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth deve ser usado dentro de AuthProvider');
  return context;
}
