"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import Dashboard from "@/components/dashboard/dashboard"

export default function DashboardPage() {
  const router = useRouter()

  useEffect(() => {
    // Check if user is authenticated
    const isAuthenticated = localStorage.getItem("isAuthenticated")
    if (!isAuthenticated) {
      router.push("/")
    }
  }, [router])

  return <Dashboard />
}
