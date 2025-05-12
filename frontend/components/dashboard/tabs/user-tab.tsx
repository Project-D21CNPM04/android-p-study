"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"

export default function UserTab({ data }: { data: any }) {
  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Tổng số User</CardTitle>
          <div className="text-2xl font-bold text-red-600">{data.users.total}</div>
        </CardHeader>
        <CardContent>
          <CardDescription>Tổng số người dùng trong hệ thống</CardDescription>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Notes nhiều nhất</CardTitle>
          <div className="text-2xl font-bold text-red-600">{data.users.maxNotes}</div>
        </CardHeader>
        <CardContent>
          <CardDescription>Số Notes nhiều nhất của một User</CardDescription>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Notes ít nhất</CardTitle>
          <div className="text-2xl font-bold text-red-600">{data.users.minNotes}</div>
        </CardHeader>
        <CardContent>
          <CardDescription>Số Notes ít nhất của một User</CardDescription>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Notes trung bình</CardTitle>
          <div className="text-2xl font-bold text-red-600">{data.users.avgNotes}</div>
        </CardHeader>
        <CardContent>
          <CardDescription>Số Notes trung bình mỗi User</CardDescription>
        </CardContent>
      </Card>

      <Card className="col-span-full">
        <CardHeader>
          <CardTitle>Notes theo User</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-[400px]">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.users.notesPerUser}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="notes" fill="#e53e3e" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
