"use client"

import { useState, useEffect } from "react"
import Link from "next/link"

export default function DashboardHeader() {
  const [user, setUser] = useState<{ email: string } | null>(null)

  useEffect(() => {
    const userData = localStorage.getItem("user")
    if (userData) {
      setUser(JSON.parse(userData))
    }
  }, [])

  return (
    <header className="fixed top-0 left-0 right-0 bg-white border-b border-gray-200 z-10">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        <div className="flex items-center">
          <Link href="/dashboard" className="flex items-center">
            <h1 className="text-xl font-bold text-red-600">PStudy</h1>
          </Link>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-sm text-gray-600">{user?.email}</div>
          <div className="h-8 w-8 rounded-full bg-red-100 flex items-center justify-center text-red-600 font-medium">
            {user?.email?.charAt(0).toUpperCase() || "U"}
          </div>
        </div>
      </div>
    </header>
  )
}
