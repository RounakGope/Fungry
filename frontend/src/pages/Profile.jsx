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
const emptyAddress = { houseNumber: '', address: '', landmark: '', state: '', zipcode: '' }
export default function Profile() {
  const { user, refreshMe } = useAuth()
  const toast = useToast()
  const [phone, setPhone] = useState(user?.phoneNumber || '')
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '', confirm: '' })
  const [addresses, setAddresses] = useState([])
  const [loading, setLoading] = useState(true)
  const [addressForm, setAddressForm] = useState(emptyAddress)
  const [editingId, setEditingId] = useState(null)
  const [deleteId, setDeleteId] = useState(null)
  const [saving, setSaving] = useState(false)

  const loadAddresses = () =>
    addressApi.getUserAddresses(user.id).then(setAddresses)

  useEffect(() => {
    loadAddresses()
      .catch((err) => toast.error(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [user.id])

  const handlePhoneUpdate = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await usersApi.updatePhone(user.id, { phone })
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
      await usersApi.updatePassword(user.id, {
        currentPassword: passwords.currentPassword,
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
        houseNumber: addressForm.houseNumber === '' ? null : Number(addressForm.houseNumber),
        zipcode: addressForm.zipcode === '' ? null : Number(addressForm.zipcode),
      }
      if (editingId) {
        await addressApi.updateAddress(editingId, user.id, { ...payload, addressId: editingId })
        toast.success('Address updated')
      } else {
        await addressApi.createAddress(user.id, payload)
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
      await addressApi.deleteAddress(deleteId, user.id)
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
    setEditingId(addr.addressId)
    setAddressForm({
      houseNumber: addr.houseNumber,
      address: addr.address,
      landmark: addr.landmark,
      state: addr.state,
      zipcode: addr.zipcode,
    })
  }

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-2xl space-y-8">
      <h1 className="text-2xl font-bold text-gray-900">Profile</h1>

      <Card>
        <h2 className="text-sm font-semibold text-gray-900">Account</h2>
        <dl className="mt-3 space-y-2 text-sm">
          <div className="flex justify-between"><dt className="text-gray-500">Username</dt><dd>{user.userName}</dd></div>
          <div className="flex justify-between"><dt className="text-gray-500">Email</dt><dd>{user.userEmail}</dd></div>
        </dl>
      </Card>

      <Card>
        <h2 className="mb-4 text-sm font-semibold text-gray-900">Update phone</h2>
        <form onSubmit={handlePhoneUpdate} className="flex gap-3">
          <Input className="flex-1" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="Phone number" />
          <Button type="submit" size="sm" disabled={saving}>Save</Button>
        </form>
      </Card>

      <Card>
        <h2 className="mb-4 text-sm font-semibold text-gray-900">Change password</h2>
        <form onSubmit={handlePasswordUpdate} className="space-y-3">
          <Input type="password" label="Current password" value={passwords.currentPassword} onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })} required />
          <Input type="password" label="New password" value={passwords.newPassword} onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })} required />
          <Input type="password" label="Confirm new password" value={passwords.confirm} onChange={(e) => setPasswords({ ...passwords, confirm: e.target.value })} required />
          <Button type="submit" size="sm" disabled={saving}>Update password</Button>
        </form>
      </Card>

      <section>
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Addresses</h2>

        <Card className="mb-4">
          <h3 className="mb-3 text-sm font-semibold text-gray-900">{editingId ? 'Edit address' : 'Add address'}</h3>
          <form onSubmit={handleSaveAddress} className="space-y-3">
            <Input label="House number" value={addressForm.houseNumber} onChange={(e) => setAddressForm({ ...addressForm, houseNumber: e.target.value })} required />
            <Input label="Address" value={addressForm.address} onChange={(e) => setAddressForm({ ...addressForm, address: e.target.value })} required />
            <Input label="Landmark" value={addressForm.landMark} onChange={(e) => setAddressForm({ ...addressForm, landMark: e.target.value })} />
            <div className="grid gap-3 sm:grid-cols-2">
              <Input label="State" value={addressForm.state} onChange={(e) => setAddressForm({ ...addressForm, state: e.target.value })} required />
              <Input label="ZIP code" value={addressForm.zipCode} onChange={(e) => setAddressForm({ ...addressForm, zipCode: e.target.value })} required />
            </div>
            <Input label="Country" value={addressForm.country} onChange={(e) => setAddressForm({ ...addressForm, country: e.target.value })} required />
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
            <Card key={addr.addressId} className="flex items-start justify-between">
              <div className="text-sm">
                <p className="font-medium text-gray-900">{addr.houseNumber}, {addr.address}</p>
                <p className="text-gray-500">{addr.landmark && `${addr.landmark}, `}{addr.state} {addr.zipcode}</p>
              </div>
              <div className="flex gap-2">
                <Button size="sm" variant="secondary" onClick={() => startEdit(addr)}>Edit</Button>
                <Button size="sm" variant="danger" onClick={() => setDeleteId(addr.addressId)}>Delete</Button>
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
