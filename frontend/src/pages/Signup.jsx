import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import * as usersApi from '../api/users'
import * as authApi from '../api/auth'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Input from '../components/Input'
import Card from '../components/Card'
import { AuthLayout } from '../components/Layout'

export default function Signup() {
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [loading, setLoading] = useState(false)
  const { refreshMe } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.password !== form.confirmPassword) {
      toast.error('Passwords do not match')
      return
    }
    setLoading(true)
    try {
      await usersApi.createUser({
        username: form.username,
        email: form.email,
        password: form.password,
      })
      await authApi.login({ userEmail: form.email, password: form.password })
      await refreshMe()
      toast.success('Account created')
      navigate('/')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout>
      <Card className="w-full max-w-sm">
        <h1 className="text-xl font-semibold text-gray-900">Create account</h1>
        <p className="mt-1 text-sm text-gray-500">Join Fungry to order food</p>
        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <Input label="Username" name="username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required />
          <Input label="Email" name="email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          <Input label="Password" name="password" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          <Input label="Confirm password" name="confirmPassword" type="password" value={form.confirmPassword} onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })} required />
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Creating…' : 'Create account'}
          </Button>
        </form>
        <p className="mt-4 text-center text-sm text-gray-500">
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-primary-700 hover:underline">Sign in</Link>
        </p>
      </Card>
    </AuthLayout>
  )
}
