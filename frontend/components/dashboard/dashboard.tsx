"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button"
import { RefreshCw, LogOut, AlertOctagon } from "lucide-react"
import { useToast } from "@/components/ui/use-toast"

import OverviewTab from "./tabs/overview-tab"
import QuizTab from "./tabs/quiz-tab"
import FlashcardTab from "./tabs/flashcard-tab"
import MindmapTab from "./tabs/mindmap-tab"
import UserTab from "./tabs/user-tab"
import DashboardHeader from "./dashboard-header"
import { getDashboardStats } from "@/lib/api"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"

export default function Dashboard() {
  const router = useRouter()
  const { toast } = useToast()
  const [data, setData] = useState(null)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [isError, setIsError] = useState(false)
  const [errorMessage, setErrorMessage] = useState("")
  const [isMockData, setIsMockData] = useState(false)

  // Fetch data on component mount
  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    setIsLoading(true)
    setIsError(false)
    setErrorMessage("")
    setIsMockData(false)

    try {
      const apiData = await getDashboardStats()
      setData(apiData)
      
      // Kiểm tra nếu đang sử dụng dữ liệu mẫu
      if (apiData.isMockData) {
        setIsMockData(true)
        toast({
          variant: "default",
          title: "Sử dụng dữ liệu mẫu",
          description: "Không thể kết nối tới backend. Đang hiển thị dữ liệu mẫu.",
        })
      }
      
      setIsLoading(false)
    } catch (error) {
      console.error("Failed to fetch dashboard data:", error)
      setIsError(true)
      setErrorMessage(error instanceof Error ? error.message : "Không thể kết nối đến server")
      setIsLoading(false)
      toast({
        variant: "destructive", 
        title: "Không thể tải dữ liệu",
        description: "Có lỗi xảy ra khi kết nối đến server.",
      })
    }
  }

  const handleRefresh = async () => {
    setIsRefreshing(true)
    
    try {
      await fetchDashboardData()
      if (!isMockData) {
        toast({
          title: "Dữ liệu đã được làm mới",
          description: "Thông tin dashboard đã được cập nhật.",
        })
      }
    } catch (error) {
      // Error already handled in fetchDashboardData
    } finally {
      setIsRefreshing(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem("isAuthenticated")
    localStorage.removeItem("user")
    localStorage.removeItem("token")
    toast({
      title: "Đăng xuất thành công",
      description: "Hẹn gặp lại bạn!",
    })
    router.push("/")
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <DashboardHeader />

      <div className="container mx-auto p-4 pt-24">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-red-600">Dashboard</h1>
          <div className="flex gap-2">
            <Button variant="outline" size="sm" onClick={handleRefresh} disabled={isRefreshing || isLoading}>
              <RefreshCw className={`h-4 w-4 mr-2 ${isRefreshing ? "animate-spin" : ""}`} />
              {isRefreshing ? "Đang làm mới..." : "Làm mới dữ liệu"}
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={handleLogout}
              className="border-red-200 text-red-600 hover:bg-red-50"
            >
              <LogOut className="h-4 w-4 mr-2" />
              Đăng xuất
            </Button>
          </div>
        </div>

        {isMockData && (
          <Alert variant="warning" className="mb-6 bg-yellow-50 text-yellow-800 border-yellow-300">
            <AlertOctagon className="h-4 w-4" />
            <AlertTitle>Đang sử dụng dữ liệu mẫu!</AlertTitle>
            <AlertDescription>
              Không thể kết nối tới backend. Dữ liệu hiển thị hiện tại chỉ là dữ liệu mẫu.
            </AlertDescription>
          </Alert>
        )}

        {isError ? (
          <div className="rounded-lg border border-red-200 bg-red-50 p-8 text-center text-red-800">
            <AlertOctagon className="h-12 w-12 mx-auto mb-4 text-red-600" />
            <h2 className="text-xl font-bold mb-2">Không thể tải dữ liệu</h2>
            <p>{errorMessage || "Có lỗi xảy ra khi kết nối đến server."}</p>
            <Button 
              variant="outline" 
              className="mt-4 border-red-300 text-red-600 hover:bg-red-100"
              onClick={handleRefresh}
            >
              Thử lại
            </Button>
          </div>
        ) : isLoading ? (
          <div className="flex justify-center items-center h-64">
            <div className="text-center">
              <RefreshCw className="h-10 w-10 animate-spin text-red-600 mx-auto mb-4" />
              <p className="text-gray-500">Đang tải dữ liệu...</p>
            </div>
          </div>
        ) : data ? (
          <Tabs defaultValue="overview" className="w-full">
            <TabsList className="grid grid-cols-5 mb-8">
              <TabsTrigger value="overview" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                Overview
              </TabsTrigger>
              <TabsTrigger value="quiz" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                Quiz
              </TabsTrigger>
              <TabsTrigger value="flashcard" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                Flashcard
              </TabsTrigger>
              <TabsTrigger value="mindmap" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                Mindmap
              </TabsTrigger>
              <TabsTrigger value="user" className="data-[state=active]:bg-red-600 data-[state=active]:text-white">
                User
              </TabsTrigger>
            </TabsList>

            <TabsContent value="overview">
              <OverviewTab data={data} />
            </TabsContent>

            <TabsContent value="quiz">
              <QuizTab data={data} />
            </TabsContent>

            <TabsContent value="flashcard">
              <FlashcardTab data={data} />
            </TabsContent>

            <TabsContent value="mindmap">
              <MindmapTab data={data} />
            </TabsContent>

            <TabsContent value="user">
              <UserTab data={data} />
            </TabsContent>
          </Tabs>
        ) : null}
      </div>
    </div>
  )
}
