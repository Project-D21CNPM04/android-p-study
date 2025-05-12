"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { PieChart, Pie, Cell, Legend, Tooltip, ResponsiveContainer } from "recharts"

export default function FlashcardTab({ data }: { data: any }) {
  const COLORS = ["#e53e3e", "#ed8936", "#38a169", "#3182ce", "#805ad5"]

  return (
    <div className="grid gap-4 md:grid-cols-2">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-xl font-bold">Tổng số Flashcard</CardTitle>
          <div className="text-3xl font-bold text-red-600">{data.flashcard.total}</div>
        </CardHeader>
        <CardContent>
          <CardDescription>Tổng số Flashcard đã được tạo trong hệ thống</CardDescription>
        </CardContent>
      </Card>

      <Card className="md:row-span-2">
        <CardHeader>
          <CardTitle>Flashcard theo loại</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-[300px]">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data.flashcard.byType}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {data.flashcard.byType.map((entry: any, index: number) => (
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

      <Card>
        <CardHeader>
          <CardTitle>Thống kê Flashcard</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {data.flashcard.byType.map((item: any, index: number) => (
              <div key={index} className="flex items-center justify-between">
                <div className="flex items-center">
                  <div
                    className="w-3 h-3 rounded-full mr-2"
                    style={{ backgroundColor: COLORS[index % COLORS.length] }}
                  />
                  <span>{item.name}</span>
                </div>
                <span className="font-medium">{item.value}</span>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
