import { Navigate, Route, Routes } from 'react-router-dom'
import { Layout } from '@/components/Layout'
import { RichiedeAuth } from '@/auth/RichiedeAuth'
import { RichiedeAdmin } from '@/auth/RichiedeAdmin'
import { Home } from '@/pages/Home'
import { RobotDettaglio } from '@/pages/RobotDettaglio'
import { Login } from '@/pages/Login'
import { Registrazione } from '@/pages/Registrazione'
import { Preferiti } from '@/pages/Preferiti'
import { Admin } from '@/pages/Admin'

/** Rotte: le pubbliche stanno sotto Layout, le protette dentro RichiedeAuth / RichiedeAdmin. */
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="robot/:id" element={<RobotDettaglio />} />
        <Route path="login" element={<Login />} />
        <Route path="registrazione" element={<Registrazione />} />

        <Route element={<RichiedeAuth />}>
          <Route path="preferiti" element={<Preferiti />} />
        </Route>

        <Route element={<RichiedeAdmin />}>
          <Route path="admin" element={<Admin />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  )
}
