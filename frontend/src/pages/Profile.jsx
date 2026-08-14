import { useEffect, useState } from 'react'
import * as addressApi from '../api/address'
import * as usersApi from '../api/users'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import Input from '../components/Input'
import Modal from '../components/Modal'
import LoadingSpinner from '../components/LoadingSpinner'

// Matches AddressCreateDTO: street, city, state, zipCode, country, isDefault
const emptyAddress = { street: '', city: '', state: '', zipCode: '', country: '', isDefault: false }

export default function Profile() {
  const { user, refreshMe } = useAuth()
  const toast = useToast()
  const [phone, setPhone] = useState(user?.phone || '')
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '', confirm: '' })
  const [addresses, setAddresses] = useState([])
  const [loading, setLoading] = useState(true)
  const [addressForm, setAddressForm] = useState(emptyAddress)
  const [editingId, setEditingId] = useState(null)
  const [deleteId, setDeleteId] = useState(null)
  const [saving, setSaving] = useState(false)

  // getUserAddresses() is session-scoped now — no userId param
  const loadAddresses = () => addressApi.getUserAddresses().then(setAddresses)

  useEffect(() => {
    loadAddresses()
      .catch((err) => toast.error(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  const handlePhoneUpdate = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      // PhoneDTO: { number: string } — single arg, no userId
      await usersApi.updatePhone({ number: phone })
      await refreshMe()
      toast.success('Phone updated')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const handlePasswordUpdate = async (e) => {
    e.preventDefault()
    if (passwords.newPassword !== passwords.confirm) {
      toast.error('New passwords do not match')
      return
    }
    setSaving(true)
    try {
      // PasswordUpdateDTO: { oldPassword, newPassword } — single arg, no userId
      await usersApi.updatePassword({
        oldPassword: passwords.currentPassword,
        newPassword: passwords.newPassword,
      })
      setPasswords({ currentPassword: '', newPassword: '', confirm: '' })
      toast.success('Password updated')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const handleSaveAddress = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...addressForm,
        zipCode: addressForm.zipCode, // AddressCreateDTO.zipCode is a string
      }
      if (editingId) {
        // updateAddress(addressId, data) — no userId, no addressId in body
        await addressApi.updateAddress(editingId, payload)
        toast.success('Address updated')
      } else {
        // createAddress(data) — no userId
        await addressApi.createAddress(payload)
        toast.success('Address added')
      }
      setAddressForm(emptyAddress)
      setEditingId(null)
      await loadAddresses()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const handleDeleteAddress = async () => {
    setSaving(true)
    try {
      // deleteAddress(addressId) — no userId
      await addressApi.deleteAddress(deleteId)
      toast.success('Address deleted')
      setDeleteId(null)
      await loadAddresses()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const startEdit = (addr) => {
    setEditingId(addr.id)
    setAddressForm({
      street: addr.street ?? '',
      city: addr.city ?? '',
      state: addr.state ?? '',
      zipCode: addr.zipCode ?? '',
      country: addr.country ?? '',
      isDefault: addr.isDefault ?? false,
    })
  }

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-2xl space-y-8">
      <h1 className="text-2xl font-bold text-white">Profile</h1>

      <Card>
        <h2 className="text-sm font-semibold text-white">Account</h2>
        <dl className="mt-3 space-y-2 text-sm">
          <div className="flex justify-between"><dt className="text-white/70">Username</dt><dd>{user.username}</dd></div>
          <div className="flex justify-between"><dt className="text-white/70">Email</dt><dd>{user.email}</dd></div>
        </dl>
      </Card>

      <Card>
        <h2 className="mb-4 text-sm font-semibold text-white">Update phone</h2>
        <form onSubmit={handlePhoneUpdate} className="flex gap-3">
          <Input className="flex-1" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="Phone number" />
          <Button type="submit" size="sm" disabled={saving}>Save</Button>
        </form>
      </Card>

      <Card>
        <h2 className="mb-4 text-sm font-semibold text-white">Change password</h2>
        <form onSubmit={handlePasswordUpdate} className="space-y-3">
          <Input type="password" label="Current password" value={passwords.currentPassword} onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })} required />
          <Input type="password" label="New password" value={passwords.newPassword} onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })} required />
          <Input type="password" label="Confirm new password" value={passwords.confirm} onChange={(e) => setPasswords({ ...passwords, confirm: e.target.value })} required />
          <Button type="submit" size="sm" disabled={saving}>Update password</Button>
        </form>
      </Card>

      <section>
        <h2 className="mb-4 text-lg font-semibold text-white">Addresses</h2>

        <Card className="mb-4">
          <h3 className="mb-3 text-sm font-semibold text-white">{editingId ? 'Edit address' : 'Add address'}</h3>
          <form onSubmit={handleSaveAddress} className="space-y-3">
            <Input label="Street" value={addressForm.street} onChange={(e) => setAddressForm({ ...addressForm, street: e.target.value })} required />
            <Input label="City" value={addressForm.city} onChange={(e) => setAddressForm({ ...addressForm, city: e.target.value })} required />
            <div className="grid gap-3 sm:grid-cols-2">
              <Input label="State" value={addressForm.state} onChange={(e) => setAddressForm({ ...addressForm, state: e.target.value })} required />
              <Input label="ZIP code" value={addressForm.zipCode} onChange={(e) => setAddressForm({ ...addressForm, zipCode: e.target.value })} required />
            </div>
            <Input label="Country" value={addressForm.country} onChange={(e) => setAddressForm({ ...addressForm, country: e.target.value })} required />
            <label className="flex items-center gap-2 text-sm text-white/70">
              <input
                type="checkbox"
                checked={addressForm.isDefault}
                onChange={(e) => setAddressForm({ ...addressForm, isDefault: e.target.checked })}
              />
              Set as default address
            </label>
            <div className="flex gap-2">
              <Button type="submit" size="sm" disabled={saving}>{editingId ? 'Update' : 'Add'}</Button>
              {editingId && (
                <Button type="button" size="sm" variant="secondary" onClick={() => { setEditingId(null); setAddressForm(emptyAddress) }}>Cancel</Button>
              )}
            </div>
          </form>
        </Card>

        <div className="space-y-2">
          {addresses.map((addr) => (
            <Card key={addr.id} className="flex items-start justify-between">
              <div className="text-sm">
                <p className="font-medium text-white">{addr.street}, {addr.city}</p>
                <p className="text-white/70">{addr.state} {addr.zipCode}{addr.isDefault ? ' · Default' : ''}</p>
              </div>
              <div className="flex gap-2">
                <Button size="sm" variant="secondary" onClick={() => startEdit(addr)}>Edit</Button>
                <Button size="sm" variant="danger" onClick={() => setDeleteId(addr.id)}>Delete</Button>
              </div>
            </Card>
          ))}
        </div>
      </section>

      <Modal
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        title="Delete address?"
        confirmLabel="Delete"
        variant="danger"
        loading={saving}
        onConfirm={handleDeleteAddress}
      >
        This address will be permanently removed.
      </Modal>
    </div>
  )
}