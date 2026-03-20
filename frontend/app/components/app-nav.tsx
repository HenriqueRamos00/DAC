import { useAuth } from "./auth-provider";
import CustomSidebarTrigger from "./custom-sidebar-trigger";
import { Avatar, AvatarFallback, AvatarImage } from "./ui/avatar";

export default function  AppNav() {
  const { auth } = useAuth()
  const user = { name: auth.nome ?? "Cliente"}
  return (
    <div className="flex bg justify-between items-center py-2 pr-3 bg-sidebar">
        <CustomSidebarTrigger />
        <div className="flex items-center gap-3 text-primary">
            <Avatar>
              <AvatarImage src="https://github.com/shadcn.png" />
              <AvatarFallback>
                {user.name[0]}
              </AvatarFallback>
            </Avatar>
          <span className="text-sm">{user.name}</span>
        </div>
    </div>
  )
}
