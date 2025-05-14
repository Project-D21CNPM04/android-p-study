"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Eye, EyeOff, Lock, Mail } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { useToast } from "@/components/ui/use-toast"
import { login } from "@/lib/api"

export default function LoginForm() {
  const [showPassword, setShowPassword] = useState(false)
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const router = useRouter()
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      const response = await login(email, password);
      
      // Store authentication token and user info
      localStorage.setItem("isAuthenticated", "true")
      localStorage.setItem("user", JSON.stringify({ email }))
      localStorage.setItem("token", response.token)

      toast({
        title: "Đăng nhập thành công",
        description: "Chào mừng bạn quay trở lại PStudy!",
      })

      // Redirect to dashboard
      router.push("/dashboard")
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Đăng nhập thất bại",
        description: error instanceof Error ? error.message : "Email hoặc mật khẩu không chính xác.",
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="w-full max-w-md mx-auto">
      <div className="rounded-lg border border-gray-200 bg-white shadow-sm p-8">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-red-600 mb-2">PStudy</h1>
          <p className="text-gray-600">Ứng dụng hỗ trợ học tập cho sinh viên PTIT</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Mail className="h-4 w-4 text-gray-400" />
                </div>
                <Input
                  id="email"
                  type="email"
                  placeholder="name@ptit.edu.vn"
                  className="pl-10"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Mật khẩu</Label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Lock className="h-4 w-4 text-gray-400" />
                </div>
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  className="pl-10"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <div
                  className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4 text-gray-400" />
                  ) : (
                    <Eye className="h-4 w-4 text-gray-400" />
                  )}
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <Checkbox id="remember" />
                <Label htmlFor="remember" className="text-sm">
                  Ghi nhớ đăng nhập
                </Label>
              </div>
              <a href="#" className="text-sm text-red-600 hover:underline">
                Quên mật khẩu?
              </a>
            </div>

            <Button type="submit" className="w-full bg-red-600 hover:bg-red-700" disabled={isLoading}>
              {isLoading ? "Đang đăng nhập..." : "Đăng nhập"}
            </Button>

            <p className="text-center text-sm text-gray-600 mt-4">
              Chưa có tài khoản?{" "}
              <a href="#" className="text-red-600 hover:underline">
                Đăng ký
              </a>
            </p>
          </div>
        </form>
      </div>
    </div>
  )
}
