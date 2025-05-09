import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ProductFormData } from "@/contexts/ProductContext";
import { uploadToCloudinary } from "@/lib/upload-helper";

const productSchema = z.object({
  productId: z.number().optional(),
  productName: z.string().min(1, "Product name is required"),
  description: z.string().min(1, "Description is required"),
  price: z.number().positive("Price must be positive"),
  quantity: z.number().min(0, "Quantity cannot be negative"),
  category: z.string().min(1, "Category is required"),
  productImage: z.string().optional(), // Make productImage optional since it might be empty initially
  image: z.any(), // This will hold the File object for new uploads
  available: z.boolean().optional(),
  root: z.any().optional(),
}).refine((data) => {
  // Custom validation to ensure either productImage or image is provided
  return data.productImage || data.image instanceof File;
}, {
  message: "Product Image is required",
  path: ["image"], // This will show the error on the image field
});

type ProductFormValues = z.infer<typeof productSchema>;

interface ProductFormProps {
  initialData?: Partial<ProductFormData>;
  onSubmit: (data: ProductFormData) => void;
  isSubmitting?: boolean;
}

const PRODUCT_CATEGORIES = ["Food", "Beverages"];

export const ProductForm: React.FC<ProductFormProps> = ({
  initialData = {},
  onSubmit,
  isSubmitting = false,
}) => {
  const [isUploading, setIsUploading] = useState(false);

  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productSchema),
    values: {
      productId: initialData.productId,
      productName: initialData.productName || "",
      description: initialData.description || "",
      price: initialData.price || 0,
      quantity: initialData.quantity || 0,
      category: initialData.category || "",
      productImage: initialData.productImage || "",
      available: initialData.available ?? true,
      image: null,
      root: undefined,
    },
    criteriaMode: "all",
    shouldUnregister: false,
  });

  // Update form when initialData changes
  useEffect(() => {
    if (initialData) {
      form.reset({
        ...form.getValues(),
        productName: initialData.productName || "",
        description: initialData.description || "",
        price: initialData.price || 0,
        quantity: initialData.quantity || 0,
        category: initialData.category || "",
        productImage: initialData.productImage || "",
        available: initialData.available ?? true,
      });
    }
  }, [initialData, form]);

  const handleSubmit = async (values: ProductFormValues) => {
    try {
      setIsUploading(true);
      form.clearErrors("root");
      let imageUrl = values.productImage;

      // If a new image file is selected, upload it
      if (values.image instanceof File) {
        try {
          const uploadResult = await uploadToCloudinary(values.image);
          imageUrl = uploadResult.url;
        } catch (error) {
          console.error("Error uploading image:", error);
          form.setError("image", { message: "Failed to upload image. Please try again." });
          return;
        }
      }

      // Use existing image if available and no new image was uploaded
      if (!imageUrl && initialData.productImage) {
        imageUrl = initialData.productImage;
      }

      // Check if we have either an uploaded image or an existing image URL
      if (!imageUrl && !values.image) {
        form.setError("image", { message: "Product Image is required" });
        return;
      }

      // Validate required fields with user-friendly messages
      if (!values.productName?.trim()) {
        form.setError("productName", { message: "Product name is required" });
        return;
      }
      if (!values.description?.trim()) {
        form.setError("description", { message: "Description is required" });
        return;
      }
      if (!values.category?.trim()) {
        form.setError("category", { message: "Category is required" });
        return;
      }
      if (!values.price || values.price <= 0) {
        form.setError("price", { message: "Price must be positive" });
        return;
      }
      if (!imageUrl) {
        form.setError("image", { message: "Product Image is required" });
        return;
      }

      const productData: ProductFormData = {
        productId: initialData.productId,
        productName: values.productName.trim(),
        description: values.description.trim(),
        price: values.price,
        quantity: values.quantity || 0,
        category: values.category.trim(),
        productImage: imageUrl,
        available: values.available ?? true,
      };

      await onSubmit(productData);
    } catch (error) {
      console.error("Error during form submission:", error);
      if (error.response?.data?.message) {
        const errorMessage = error.response.data.message;
        if (errorMessage.includes("productName")) {
          form.setError("productName", { message: "Product name is required" });
        }
        if (errorMessage.includes("description")) {
          form.setError("description", { message: "Description is required" });
        }
        if (errorMessage.includes("category")) {
          form.setError("category", { message: "Category is required" });
        }
        if (errorMessage.includes("price")) {
          form.setError("price", { message: "Price must be positive" });
        }
        if (errorMessage.includes("productImage")) {
          form.setError("image", { message: "Product Image is required" });
        }
      } else {
        form.setError("root", { message: "Failed to save product. Please try again." });
      }
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        {form.formState.errors.root?.message && (
          <div className="rounded-lg border border-destructive bg-destructive/10 p-4 shadow-sm">
            <div className="flex items-center justify-center gap-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-4 w-4 text-destructive"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                  clipRule="evenodd"
                />
              </svg>
              <p className="text-sm font-medium text-destructive">
                {form.formState.errors.root.message || "An error occurred while saving the product."}
              </p>
            </div>
          </div>
        )}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <FormField
            control={form.control}
            name="productName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Product Name</FormLabel>
                <FormControl>
                  <Input {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="category"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Category</FormLabel>
                <Select onValueChange={field.onChange} value={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Select a category" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    {PRODUCT_CATEGORIES.map((category) => (
                      <SelectItem key={category} value={category}>
                        {category}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Description</FormLabel>
              <FormControl>
                <Textarea className="min-h-[100px]" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="price"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Price ($)</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    step="0.01"
                    {...field}
                    onChange={(e) => field.onChange(parseFloat(e.target.value || "0"))}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="quantity"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Quantity</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    {...field}
                    onChange={(e) => field.onChange(parseInt(e.target.value || "0"))}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="image"
          render={({ field: { value, onChange, ...field } }) => (
            <FormItem>
              <FormLabel>Product Image</FormLabel>
              <FormControl>
                <Input
                  type="file"
                  accept="image/*"
                  onChange={(e) => {
                    const file = e.target.files?.[0];
                    if (file) {
                      onChange(file);
                    }
                  }}
                  {...field}
                />
              </FormControl>
              <FormDescription>Upload a product image</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {(form.watch("image") instanceof File || initialData.productImage) && (
          <div className="mt-4">
            <p className="text-sm font-medium mb-2">Image Preview</p>
            <div className="aspect-video w-full max-w-md mx-auto overflow-hidden rounded-lg border border-hko-border bg-slate-50">
              <img
                src={
                  form.watch("image") instanceof File
                    ? URL.createObjectURL(form.watch("image"))
                    : initialData.productImage
                }
                alt="Product preview"
                className="object-contain w-full h-full"
                onError={(e) => {
                  const target = e.target as HTMLImageElement;
                  target.src =
                    "https://placehold.co/600x400?text=Invalid+Image+URL";
                }}
              />
            </div>
          </div>
        )}

        <Button
          type="submit"
          className="w-full"
          disabled={isSubmitting || isUploading}
        >
          {isSubmitting || isUploading
            ? "Saving..."
            : initialData.productId
            ? "Update Product"
            : "Add Product"}
        </Button>
      </form>
    </Form>
  );
};
