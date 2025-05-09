import React from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";
import axios from "axios";
import { Badge } from "@/components/ui/badge";

interface Order {
  orderId: number;
  email: string;
  orderStatus: string;
  orderDate: string;
  totalCost: number;
}

interface OrderHistoryModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const baseUrl = import.meta.env.VITE_BASE_URL;

export function OrderHistoryModal({ isOpen, onClose }: OrderHistoryModalProps) {
  const [orders, setOrders] = React.useState<Order[]>([]);
  const [isLoading, setIsLoading] = React.useState(false);
  const [isCancelling, setIsCancelling] = React.useState<number | null>(null);

  const fetchOrders = React.useCallback(async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem("token");
      if (!token) return;

      const response = await axios.get(`${baseUrl}/user/orders`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOrders(response.data);
    } catch (error) {
      console.error("Error fetching orders:", error);
      toast.error("Failed to load order history");
    } finally {
      setIsLoading(false);
    }
  }, []);

  React.useEffect(() => {
    if (isOpen) {
      fetchOrders();
    }
  }, [isOpen, fetchOrders]);

  const handleCancelOrder = async (orderId: number) => {
    try {
      setIsCancelling(orderId);
      const token = localStorage.getItem("token");
      if (!token) {
        toast.error("Please log in");
        return;
      }

      await axios.put(
        `${baseUrl}/user/orders/${orderId}/cancel`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      toast.success("Order cancelled successfully");
      fetchOrders();
    } catch (error) {
      console.error("Error cancelling order:", error);
      toast.error("Failed to cancel order");
    } finally {
      setIsCancelling(null);
    }
  };

  const getStatusBadgeColor = (status: string) => {
    switch (status.toUpperCase()) {
      case "PENDING":
        return "bg-yellow-100 text-yellow-800 border-yellow-200";
      case "PROCESSING":
        return "bg-blue-100 text-blue-800 border-blue-200";
      case "COMPLETED":
        return "bg-green-100 text-green-800 border-green-200";
      case "CANCELLED":
        return "bg-red-100 text-red-800 border-red-200";
      default:
        return "bg-gray-100 text-gray-800 border-gray-200";
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>Order History</DialogTitle>
          <DialogDescription>View your past orders and their status</DialogDescription>
        </DialogHeader>

        <ScrollArea className="max-h-[60vh] mt-4">
          {isLoading ? (
            <div className="flex items-center justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-hko-primary" />
            </div>
          ) : orders.length === 0 ? (
            <div className="text-center py-8 text-hko-text-secondary">
              No orders found
            </div>
          ) : (
            <div className="space-y-4">
              {orders.map((order) => (
                <div
                  key={order.orderId}
                  className="border rounded-lg p-4 space-y-3"
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-medium">Order #{order.orderId}</div>
                      <div className="text-sm text-hko-text-secondary">
                        {new Date(order.orderDate).toLocaleDateString()} {" "}
                        {new Date(order.orderDate).toLocaleTimeString()}
                      </div>
                    </div>
                    <Badge
                      variant="secondary"
                      className={`${getStatusBadgeColor(order.orderStatus)}`}
                    >
                      {order.orderStatus}
                    </Badge>
                  </div>
                  <div className="text-sm">
                    <span className="font-medium">Total:</span> ₱{order.totalCost.toFixed(2)}
                  </div>
                  {!["COMPLETED", "CANCELLED"].includes(order.orderStatus.toUpperCase()) && (
                    <Button
                      variant="destructive"
                      size="sm"
                      disabled={isCancelling === order.orderId}
                      onClick={() => handleCancelOrder(order.orderId)}
                    >
                      {isCancelling === order.orderId ? "Cancelling..." : "Cancel Order"}
                    </Button>
                  )}
                </div>
              ))}
            </div>
          )}
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
