import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { getErrorMessage, ROLES } from '../utils/constants'
import Button from '../components/Button'
import Input from '../components/Input'
import Card from '../components/Card'
import { AuthLayout } from '../components/Layout'

export default function Login() {
  const [form, setForm] = useState({ userEmail: '', password: '' })
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const { role } = await login(form)
      toast.success('Signed in successfully')
      navigate(role === ROLES.RESTAURANT_OWNER ? '/restaurant-dashboard' : '/', { replace: true })
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout>
      <Card className="w-full max-w-md p-5 sm:p-6">
        <h1 className="text-xl font-semibold text-white">Sign in</h1>
        <p className="mt-1 text-sm text-white/70">Enter your credentials to continue</p>
        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <Input
            label="Email"
            name="userEmail"
            value={form.userEmail}
            onChange={(e) => setForm({ ...form, userEmail: e.target.value })}
            required
            autoComplete="email"
          />
          <Input
            label="Password"
            name="password"
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
            autoComplete="current-password"
          />
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </Button>
        </form>
        <p className="mt-4 text-center text-sm text-white/70">
          No account?{' '}
          <Link to="/signup" className="font-medium text-primary-700 hover:underline">Create one</Link>
        </p>
      </Card>
    </AuthLayout>
  )
}
