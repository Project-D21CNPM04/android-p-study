"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts"

export default function OverviewTab({ data }: { data: any }) {
  const COLORS = ["#e53e3e", "#ed8936", "#38a169", "#3182ce", "#805ad5"]

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      <Card className="col-span-full">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-xl font-bold">Tổng quan Notes</CardTitle>
          <div className="text-3xl font-bold text-red-600">{data.notes.total}</div>
        </CardHeader>
        <CardContent>
          <CardDescription>Tổng số Notes đã được tạo trong hệ thống</CardDescription>
        </CardContent>
      </Card>

      <Card className="col-span-full md:col-span-2">
        <CardHeader>
          <CardTitle>Notes theo thời gian</CardTitle>
        </CardHeader>
        <CardContent className="pl-2">
          <Tabs defaultValue="hourly">
            <TabsList className="mb-4">
              <TabsTrigger value="hourly" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                Theo giờ
              </TabsTrigger>
              <TabsTrigger value="daily" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                Theo ngày
              </TabsTrigger>
            </TabsList>
            <TabsContent value="hourly">
              <div className="h-[300px]">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={data.notes.byHour}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="value" fill="#e53e3e" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </TabsContent>
            <TabsContent value="daily">
              <div className="h-[300px]">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={data.notes.byDay}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="value" fill="#e53e3e" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>

      <Card className="col-span-full md:col-span-1">
        <CardHeader>
          <CardTitle>Notes theo loại</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-[300px]">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data.notes.byType}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {data.notes.byType.map((entry: any, index: number) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
